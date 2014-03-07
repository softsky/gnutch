import gnutch.UrlEscaper

import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.cache.CacheConstants
import org.apache.camel.component.http4.HttpOperationFailedException
import org.apache.commons.codec.binary.Base64;

class GnutchRoutes extends RouteBuilder {
    def grailsApplication

    @Override
    void configure() {
        def config = grailsApplication?.config

        onException(HttpOperationFailedException).
                handled(true).
                logStackTrace(true).
                log(LoggingLevel.ERROR, 'gnutch', 'HTTP ${exception.statusCode} Exception: ${headers.contextURI}').
                filter().
                groovy('exchange.getProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT).hasRedirectLocation()').
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
         input route.
         */
        from("${config.gnutch.inputRoute}").
                routeId('inputRoute').startupOrder(10).
                convertBodyTo(org.w3c.dom.Document).
                beanRef('sourceUrlsProducerService', 'produce')

        /**
         Main crawling routes
         */

        // link crawler route
        from("activemq:input-url?concurrentConsumers=${config.gnutch.crawl.threads * config.gnutch.crawl.multiplier * 2}").
                routeId('inputUrl').startupOrder(9).
                setHeader('contextURI', body(String)). // duplicating original uri in contextURI header
                setHeader(Exchange.HTTP_URI, body(String)).
                setHeader(Exchange.HTTP_METHOD, constant('GET')).
                process { ex ->
                    // setting up custom headers
                    config.gnutch.http.customHeaders.each { header ->
                        header.each { k, v ->
                            ex.in.headers[k] = v
                        }
                    }
                }.
                removeHeader(Exchange.HTTP_QUERY).
                removeHeader(Exchange.CONTENT_TYPE).
                setBody(constant()).
                log(LoggingLevel.DEBUG, 'gnutch', 'Retrieving ${headers.contextURI}').
                to('http4://null'). // invoking HttpClient
                process { ex ->
                    (config.gnutch.handlers.postHTTP as org.apache.camel.Processor).process(ex)
                }.
                choice().
        // for text/html Content-type we unmarshall with Tidy, extracting sublinks and index page
                when(header('Content-Type').contains("text/html")).
                log(LoggingLevel.OFF, 'gnutch', 'Sending to process-html').
                to("direct:process-html").
        //to("seda:process-html?size=1024&blockWhenFull=true"). // ?size=2048&blockWhenFull=true
                otherwise().
                log(LoggingLevel.OFF, 'gnutch', 'Sending to process-binary').
                to("direct:process-binary").
        //to("seda:process-binary?size=1024&blockWhenFull=true").
                end()

        // Processing Tidy entries
        //from("seda:process-html?concurrentConsumers=${config.gnutch.crawl.threads * config.gnutch.crawl.multiplier}").
        from("direct:process-html").
                routeId('sedaProcessTidy').startupOrder(8).
                to("direct:process-tidy")

        // Processing Tika entries
        //from("seda:process-binary?concurrentConsumers=${config.gnutch.crawl.threads * config.gnutch.crawl.multiplier}").
        from("direct:process-binary").
                routeId('sedaProcessTika').startupOrder(7).
                to("direct:process-tika")

        from("direct:process-tidy").
                routeId('processTidy').startupOrder(5).
                log(LoggingLevel.TRACE, 'gnutch', 'Processing with Tidy').
                unmarshal().tidyMarkup().
                process { ex -> (config.gnutch.handlers.postXHTML as org.apache.camel.Processor).process(ex) }.
                multicast().
        // extracting links
                to('direct:extract-links').
        // indexing, if page should be indexed
                filter().method('documentIndexer', 'isIndexable').
                log(LoggingLevel.OFF, 'gnutch', 'Sending to index-html').
                to('direct:index-xhtml'). // submitting page XHTML for future processing
                end().
                end()

        from("direct:process-tika").
                routeId('processTika').startupOrder(4).
                log(LoggingLevel.TRACE, 'gnutch', 'Processing with Tika').
                filter().method('documentIndexer', 'isIndexable').
                beanRef('tikaContentExtractor', 'extract').
                to('direct:index-binary'). // submitting tika for future processing
                end()

        // links extractor route
        from('direct:extract-links').
                routeId('extractLinks').startupOrder(6).
                setHeader('contextBase').xpath('//base/@href', String.class). // setting contextBase using //base/@href value
        // extracting links
                split(xpath('//a/@href|//iframe/@src')). // extracting all a/@href and iframe/@src
                process { ex -> ex.in.body = ex.in.body.value }. // extracting AttrNodeImpl.getValue()
        // we don't process #anchor only links and empty exchange.in.body
                filter().simple('${in.body} not regex "#.*"'). // we don't process URLs which starts with #
                log(LoggingLevel.TRACE, 'gnutch', 'Resolving URL: ${body}').
                processRef('contextUrlResolver').
                filter().
                simple('${in.body} != null'). // we don't process exchange.in.body == null
                filter().
                method('regexUrlChecker', 'check'). // submitting only those that match
        // Prepare headers
                setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_GET)).
                setHeader(CacheConstants.CACHE_KEY, body()).
                to("cache://processedUrlCache").
        // Check if entry was not found
                choice().
                when(header(CacheConstants.CACHE_ELEMENT_WAS_FOUND).isNull()).
                log(LoggingLevel.TRACE, 'gnutch', 'Element was not found in cache: ${body}').
        // Adding contextURI entry to cache
                log(LoggingLevel.TRACE, 'gnutch', 'Adding to cache: ${body}').
                setHeader(CacheConstants.CACHE_OPERATION, constant(CacheConstants.CACHE_OPERATION_ADD)).
                setHeader(CacheConstants.CACHE_KEY, body()).
                to("cache://processedUrlCache").
        // If not found, get the payload and put it to cache
                process { ex -> ex.in.body = ex.in.body.replaceAll(/\s/, '%20') }.
                to('activemq:input-url').
                otherwise().
                log(LoggingLevel.TRACE, 'gnutch', 'Ignoring ${body} as it\'s cached').
                end().
                end().
                end()

        /**
         Indexing routes
         */
        from('direct:index-xhtml').
                routeId('indexHtml').startupOrder(3).
                beanRef('documentIndexer', 'index').
                log(LoggingLevel.TRACE, 'gnutch', 'Indexed: ${body}').
                convertBodyTo(org.w3c.dom.Document).
                process { ex -> (config.gnutch.handlers.postXML as org.apache.camel.Processor).process(ex) }.
                to('direct:aggregate-documents')

        from('direct:index-binary').
                routeId('indexBinary').startupOrder(2).
                process { ex ->
                    def writer = new StringWriter()
                    def xml = new groovy.xml.MarkupBuilder(writer)
                    def title = ex.in.headers['Tika-Metadata']['title']?.trim()
                    def firstSentance = ex.in.body.split(/\./)[0]
                    xml.doc() {
                        field(name: 'id', ex.in.headers['contextURI'])
                        field(name: 'title', (title ? title : (firstSentance ? firstSentance : 'No title')).trim())
                        field(name: 'content', ex.in.body.trim())
                    }
                    ex.in.body = writer.toString()
                }.
                convertBodyTo(org.w3c.dom.Document).
                process { ex -> (config.gnutch.handlers.postXML as org.apache.camel.Processor).process(ex) }.
                to('direct:aggregate-documents')

        from('direct:aggregate-documents').
                routeId('aggregation').startupOrder(1).
                convertBodyTo(org.w3c.dom.Document).
                choice().
                when(config.gnutch.handlers.validate).
                log(LoggingLevel.DEBUG, 'gnutch', 'Indexing ${headers.contextURI}').
                aggregate(constant(true)).completionInterval(config.gnutch.aggregationTime).groupExchanges().
                processRef('docsAggregator').
                log(LoggingLevel.INFO, 'gnutch', 'Committing index ${body.getElementsByTagName("doc").length}').
                to('direct:publish').
                end().
                otherwise().
                log(LoggingLevel.DEBUG, 'gnutch', 'Ignoring ${headers.contextURI}').
                beanRef('invalidDocumentCollectorService', 'collect').
                end()

        if (config.gnutch.handlers.publish) {
            config.gnutch.handlers.publish.delegate = this
            config.gnutch.handlers.publish.call()
        }
    }
}