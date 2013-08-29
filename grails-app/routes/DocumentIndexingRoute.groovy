import org.apache.camel.Exchange
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder


class DocumentIndexingRoute extends RouteBuilder {
  def grailsApplication

  @Override  
  void configure(){
      def config = grailsApplication?.config

      from('direct:index-xhtml').
         log(LoggingLevel.DEBUG, 'gnutch', 'Indexing ${headers.contextURI}').
         beanRef('documentIndexer', 'index').
         log(LoggingLevel.TRACE, 'gnutch','Indexed: ${body}').
         process { ex -> (config.gnutch.postProcessorXML as org.apache.camel.Processor).process(ex) }.
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
         log(LoggingLevel.DEBUG, 'gnutch', 'Indexing ${headers.contextURI}').
         process { ex -> (config.gnutch.postProcessorXML as org.apache.camel.Processor).process(ex) }.
         to('direct:aggregate-documents')

        
      from('direct:aggregate-documents').
      aggregate(constant('null')).completionInterval(60000L).groupExchanges().
        processRef('docsAggregator').
        log(LoggingLevel.DEBUG, 'gnutch','Committing index').
        to('direct:publish')

        config.gnutch.publish.delegate = this
        config.gnutch.publish.call()

    }
}
