import org.apache.camel.Exchange

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.apache.camel.LoggingLevel


class DocumentIndexingRoute {
    def configure = {
      from('direct:index-page').
         beanRef('documentIndexer', 'index').
         log(LoggingLevel.TRACE, 'gnutch','Indexed: ${body}').
         to('seda:aggregate-documents')
        
      from('seda:aggregate-documents').
      aggregate(constant('null')).completionInterval(60000L).groupExchanges().
        processRef('docsAggregator').
        log(LoggingLevel.TRACE, 'gnutch','Indexed: ${body}').
        setHeader(Exchange.HTTP_URI, constant("${CH.config.gnutch.solr.serverUrl}/update?commit=true")). 
        setHeader(Exchange.HTTP_METHOD, constant('POST')).
        setHeader(Exchange.CONTENT_TYPE, constant('application/xml')).
        to("http://null")
    }
}
