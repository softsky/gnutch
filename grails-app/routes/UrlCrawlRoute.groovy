import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.component.cache.CacheConstants
import org.apache.camel.component.http.HttpOperationFailedException

import gnutch.indexer.DocumentIndexer

class UrlCrawlRoute {
    def configure = {

      onException(HttpOperationFailedException).
      handled(true).
      logStackTrace(false).
      filter().
      groovy ('exchange.getProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT).hasRedirectLocation()').
      log(LoggingLevel.TRACE, 'gnutch', 'Redirect to ${exception.redirectLocation} found. Original url: ${body}').
      process { exchange ->
        def exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class)
        exchange.in.body = exception.redirectLocation
      }.
      to('activemq:input-url')

      // link crawler route
      from("activemq:input-url?concurrentConsumers=${CH.config.gnutch.crawl.threads}").
       setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_GET)).
       setHeader(CacheConstants.CACHE_KEY, body()).
       to("cache://processedUrlCache").
       // Check if entry was not found
       choice().
       when(header(CacheConstants.CACHE_ELEMENT_WAS_FOUND).isNull()).
        log(LoggingLevel.TRACE, 'gnutch', 'Processing ${body}').
        setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_ADD)).
        setHeader(CacheConstants.CACHE_KEY, body()).
        to("cache://processedUrlCache").

        setHeader('contextURI', body(String)). // duplicating original uri in contextURI header
        setHeader(Exchange.HTTP_URI, body(String)). 
        log(LoggingLevel.TRACE, 'gnutch', 'Processing ${headers.contextURI}').
        to('http://null'). // invoking HttpClient
        unmarshal().tidyMarkup().
        multicast().
          // extracting links
          to('direct:extract-links').
          // indexing, if page should be indexed
          filter().method('documentIndexer', 'isIndexable').
            log(LoggingLevel.DEBUG, 'gnutch', 'Indexing ${headers.contextURI}').
            to('direct:index-page'). // submitting page XHTML for future processing
          end().
        end().
      end()

        // links extractor route
     from('direct:extract-links').
       // extracting links
       split(xpath('//a/@href')). // extracting all a/@href 
         setBody(simple('${body.value}')). // extracting AttrNodeImpl.getValue()
         processRef('contextUrlResolver').
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
             //to('log:gnutch?level=DEBUG&showAll=false&showBody=true').
             to('activemq:input-url').
             otherwise().
             log(LoggingLevel.TRACE, 'gnutch', 'Ignoring ${body} as it\'s cached').
          end()
    }
}
