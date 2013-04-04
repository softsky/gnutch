import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder


class DocumentIndexingRoute extends RouteBuilder {
  def grailsApplication

  @Override
  void configure() {
      def config = grailsApplication?.config

      from('direct:index-page').
         log(LoggingLevel.DEBUG, 'gnutch', 'Indexing ${headers.contextURI}').
         beanRef('documentIndexer', 'index').
         log(LoggingLevel.TRACE, 'gnutch','Indexed: ${body}').
         process{ ex -> (config.gnutch.postProcessorXML as org.apache.camel.Processor).process(ex) }.
         to('seda:aggregate-documents')
        
      from('seda:aggregate-documents').
      aggregate(constant('null')).completionInterval(60000L).groupExchanges().
        processRef('docsAggregator').
        log(LoggingLevel.DEBUG, 'gnutch','Committing index').
        setHeader(Exchange.HTTP_URI, constant("${config.gnutch.solr.serverUrl}/update?commit=true")). 
        setHeader(Exchange.HTTP_METHOD, constant('POST')).
        setHeader(Exchange.CONTENT_TYPE, constant('application/xml')).
        to("http://null")
    }
}
