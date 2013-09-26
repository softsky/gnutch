import gnutch.UrlEscaper 

import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.cache.CacheConstants
import org.apache.camel.component.http.HttpOperationFailedException
import org.apache.commons.codec.binary.Base64;

class GnutchRoutes extends RouteBuilder {
  def grailsApplication

  @Override
  void configure() {
      def config = grailsApplication?.config


      onException(HttpOperationFailedException).
        handled(true).
        logStackTrace(false).
        log(LoggingLevel.ERROR, 'gnutch', 'HTTP 404 Exception: ${headers.contextURI}').
        filter().
          groovy ('exchange.getProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT).hasRedirectLocation()').
        log(LoggingLevel.TRACE, 'gnutch', 'Redirect to ${exception.redirectLocation} found. Original url: ${body}').
        process { exchange ->
          def exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class)
          exchange.in.body = exception.redirectLocation
        }.
        to('activemq:input-url')

      onException().
        handled(true).
        logStackTrace(false).
        log(LoggingLevel.ERROR, 'gnutch', 'EXCEPTION: ${exception.message}: ${headers.contextURI}').
        beanRef('exceptionCollectorService', 'collectException')

      /**
        Input route. 
      */
      from("${config.gnutch.inputRoute}").
        convertBodyTo(org.w3c.dom.Document).
        beanRef('sourceUrlsProducerService', 'produce')

      /**
        Main crawling routes
      */
      
      // link crawler route
      from("activemq:input-url?concurrentConsumers=${config.gnutch.crawl.threads}").
        setHeader('contextURI', body(String)). // duplicating original uri in contextURI header
        setHeader(Exchange.HTTP_URI, body(String)). 
        setBody(constant()).
        log(LoggingLevel.DEBUG, 'gnutch', 'Retrieving ${headers.contextURI}').
        to('http://null'). // invoking HttpClient
        process {ex -> 
          byte [] bytes = Base64.encodeBase64(ex.in.headers[Exchange.HTTP_URI].bytes)
          // base64 encoded file might contain slash (/), replacing it with underline (_)
          // and splitting the resulted string into 100-chars chunks 
          // to prevent from `File name too long` exception
          String [] fileNameParts = UrlEscaper.split(new String(bytes).replaceAll("/", "_"), 100)
          ex.in.headers[Exchange.FILE_NAME] = fileNameParts.join('/')
        }.
        choice().
        // for text/html Content-type we unmarshall with Tidy, extracting sublinks and index page
        when(header('Content-Type').contains("text/html")).
          to("seda:process-html").
       otherwise().
          to("seda:process-binary").
       end() 

       // Processing Tidy entrie
      from("seda:process-html?concurrentConsumers=${config.gnutch.crawl.threads * 2}").
        process {ex -> 
          String fileName = ex.in.headers[Exchange.FILE_NAME].replaceAll(/\//,'')
          byte [] bytes = Base64.decodeBase64(fileName.bytes);
          ex.in.headers[Exchange.HTTP_URI] = new String(bytes).replaceAll("_", "/")
        }.
        setHeader('contextURI', header(Exchange.HTTP_URI)). // duplicating original uri in contextURI header
        to("direct:process-tidy")

       // Processing Tika entrie
      from("seda:process-binary?concurrentConsumers=${config.gnutch.crawl.threads * 2}").
        process {ex -> 
          String fileName = ex.in.headers[Exchange.FILE_NAME].replaceAll(/\//,'')
          byte [] bytes = Base64.decodeBase64(fileName.bytes);
          ex.in.headers[Exchange.HTTP_URI] = new String(bytes).replaceAll("_", "/")
        }.
        setHeader('contextURI', header(Exchange.HTTP_URI)). // duplicating original uri in contextURI header
        to("direct:process-tika")



     from("direct:process-tidy").
       log(LoggingLevel.TRACE, 'gnutch', 'Processing with Tidy').
       unmarshal().tidyMarkup().
       log(LoggingLevel.OFF, 'gnutch', body().toString()).
       process { ex -> (config.gnutch.handlers.postXHTML as org.apache.camel.Processor).process(ex) }.
       multicast().
         // extracting links
         to('direct:extract-links').
         // indexing, if page should be indexed
         filter().method('documentIndexer', 'isIndexable').
           to('direct:index-xhtml'). // submitting page XHTML for future processing
         end().
      end()

     from("direct:process-tika").
       log(LoggingLevel.TRACE, 'gnutch', 'Processing with Tika').
       filter().method('documentIndexer', 'isIndexable').
         beanRef('tikaContentExtractor', 'extract').
         to('direct:index-binary'). // submitting tika for future processing
         end()



      // links extractor route
     from('direct:extract-links').
       setHeader('contextBase', xpath('//base/@href')). // setting contextBase using //base/@href value
       // extracting links
       split(xpath('//a/@href|//iframe/@src')). // extracting all a/@href and iframe/@src
       process { ex -> ex.in.body = ex.in.body.value }. // extracting AttrNodeImpl.getValue()
         // we don't process #anchor only links and empty exchange.in.body
       filter().simple('${in.body} not regex "#.*"'). // we don't process URLs which starts with #
         log(LoggingLevel.TRACE, 'gnutch', 'Resolving URL: ${body}').
         processRef('contextUrlResolver').
          filter().
            simple ('${in.body} != null'). // we don't process exchange.in.body == null
          filter().
            method('regexUrlChecker', 'check'). // submitting only those that match
            // Prepare headers
            setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_GET)).
            setHeader(CacheConstants.CACHE_KEY, body()).
            to("cache://processedUrlCache").
            // Check if entry was not found
            choice().
             when(header(CacheConstants.CACHE_ELEMENT_WAS_FOUND).isNull()).
             // If not found, get the payload and put it to cache
             process {ex -> ex.in.body = ex.in.body.replaceAll(/\s/, '%20')}.
             log(LoggingLevel.TRACE, 'gnutch', 'Sending to activemq:input-url ${body}').
             to('activemq:input-url').
             // Adding contextURI entry to cache
             log(LoggingLevel.TRACE, 'gnutch', 'Adding to cache: ${body}').
             setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_ADD)).
             setHeader(CacheConstants.CACHE_KEY, body()).
             to("cache://processedUrlCache").
             otherwise().
             log(LoggingLevel.TRACE, 'gnutch', 'Ignoring ${body} as it\'s cached').
          end().
         end().
       end()

       /**
          Indexing routes
        */
      from('direct:index-xhtml').
      routeId('Tidy').
         beanRef('documentIndexer', 'index').
         log(LoggingLevel.TRACE, 'gnutch','Indexed: ${body}').
         process { ex -> (config.gnutch.handlers.postXML as org.apache.camel.Processor).process(ex) }.
         to('direct:aggregate-documents')

      from('direct:index-binary').
         process { ex -> 
           def writer = new StringWriter()
           def xml = new groovy.xml.MarkupBuilder(writer)
           xml.doc(){
             field(name:'id', ex.in.headers['contextURI'])
             field(name:'title', ex.in.headers['Tika-Metadata']['title']?.trim())
             field(name:'content', ex.in.body.trim())
           }
           ex.in.body = writer.toString()
         }.
         convertBodyTo(org.w3c.dom.Document).
         process { ex -> (config.gnutch.handlers.postXML as org.apache.camel.Processor).process(ex) }.
         to('direct:aggregate-documents')

      from('direct:aggregate-documents').
       choice().
       when(config.gnutch.handlers.validate).
         log(LoggingLevel.DEBUG, 'gnutch', 'Indexing ${headers.contextURI}').
         aggregate(constant('null')).completionInterval(60000L).groupExchanges().
           processRef('docsAggregator').
           log(LoggingLevel.INFO, 'gnutch','Committing index').
           to('direct:publish').
       end().
       otherwise().
         log(LoggingLevel.DEBUG, 'gnutch', 'Ignoring ${headers.contextURI}').
         beanRef('invalidDocumentCollectorService', 'collect').
       end()

        config.gnutch.handlers.publish.delegate = this
        config.gnutch.handlers.publish.call()

    }
}
