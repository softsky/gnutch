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
         beanRef('documentIndexer', 'index').
         process { ex -> (config.gnutch.handlers.postXML as Processor).process(ex) }.
         to('seda:aggregate-documents')

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
         convertBodyTo(Document).
         process { ex -> (config.gnutch.handlers.postXML as Processor).process(ex) }.
         to('seda:aggregate-documents')

        
      from('seda:aggregate-documents').
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

        config.gnutch.publish.delegate = this
        config.gnutch.publish.call()

    }
}
