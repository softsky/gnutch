import groovy.xml.MarkupBuilder

import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.w3c.dom.Document

class DocumentIndexingRoute extends RouteBuilder {
  def grailsApplication

  @Override
  void configure(){
    def config = grailsApplication?.config

    from('direct:index-xhtml').
      log(LoggingLevel.DEBUG, 'gnutch', 'Indexing ${headers.contextURI}').
      beanRef('documentIndexer', 'index').
      log(LoggingLevel.TRACE, 'gnutch','Indexed: ${body}').
      process { ex -> (config.gnutch.postProcessorXML as Processor).process(ex) }.
      to('direct:aggregate-documents')

    from('direct:index-binary').
      process { ex ->
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.doc(){
          field(name:'id', ex.in.headers['contextURI'])
          field(name:'title', ex.in.headers['Tika-Metadata']['title']?.trim())
          field(name:'content', ex.in.body.trim())
        }
        ex.in.body = writer.toString()
      }.
      convertBodyTo(Document).
      log(LoggingLevel.DEBUG, 'gnutch', 'Indexing ${headers.contextURI}').
      process { ex -> (config.gnutch.postProcessorXML as Processor).process(ex) }.
      to('direct:aggregate-documents')

    from('direct:aggregate-documents').
      aggregate(constant('null')).completionInterval(60000L).groupExchanges().
      processRef('docsAggregator').
      log(LoggingLevel.DEBUG, 'gnutch','Committing index').
      setHeader(Exchange.HTTP_URI, constant("${config.gnutch.solr.serverUrl}/update?commit=true")).
      setHeader(Exchange.HTTP_METHOD, constant('POST')).
      setHeader(Exchange.CONTENT_TYPE, constant('application/xml')).
      to("http://null")
  }
}
