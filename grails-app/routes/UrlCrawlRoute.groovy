import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.cache.CacheConstants
import org.apache.camel.component.http.HttpOperationFailedException

class UrlCrawlRoute extends RouteBuilder {
  def grailsApplication

  @Override
  void configure() {
      def config = grailsApplication?.config

      onException(UnknownHostException).
      handled(true).
      logStackTrace(false).
      log(LoggingLevel.TRACE, 'gnutch', '${headers.exception}: ${body}')

      onException(HttpOperationFailedException).
      handled(true).
      logStackTrace(false).
      log(LoggingLevel.TRACE, 'gnutch', 'HTTP 404 Exception: ${body}').
      filter().
      groovy ('exchange.getProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT).hasRedirectLocation()').
      log(LoggingLevel.TRACE, 'gnutch', 'Redirect to ${exception.redirectLocation} found. Original url: ${body}').
      process { exchange ->
        def exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable)
        exchange.in.body = exception.redirectLocation
      }.
      to('activemq:input-url')

      // link crawler route
      from("activemq:input-url?concurrentConsumers=${config.gnutch.crawl.threads}").
        delay(500).
        setHeader('contextURI', body(String)). // duplicating original uri in contextURI header
        setHeader(Exchange.HTTP_URI, body(String)).
        setBody(constant()).
        log(LoggingLevel.DEBUG, 'gnutch', 'Retrieving ${headers.contextURI}').
        to('http://null'). // invoking HttpClient

        choice().
        // for text/html Content-type we unmarshall with Tidy, extracting sublinks and index page
        when(header('Content-Type').contains("text/html")).
          log(LoggingLevel.TRACE, 'gnutch', 'Processing with Tidy').
          unmarshal().tidyMarkup().
          log(LoggingLevel.OFF, 'gnutch', body().toString()).
          process { ex -> (config.gnutch.postProcessorHTML as org.apache.camel.Processor).process(ex) }.
          multicast().
            // extracting links
            to('direct:extract-links').
            // indexing, if page should be indexed
            filter().method('documentIndexer', 'isIndexable').
              to('direct:index-xhtml'). // submitting page XHTML for future processing
            end().
          end().
       otherwise().
         log(LoggingLevel.TRACE, 'gnutch', 'Processing with Tika').
         filter().method('documentIndexer', 'isIndexable').
           beanRef('tikaContentExtractor', 'extract').
           to('direct:index-binary'). // submitting tika for future processing
         end().
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
            simple ('${in.body} != null'). // we don't process ${in.body} == null
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
    }
}
