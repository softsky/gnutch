import org.apache.camel.Exchange

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH


class DocumentIndexingRoute {
    def configure = {
      from('direct:index-page').
         beanRef('documentIndexer', 'index').
         log('Indexing: ${body}').
         to('seda:aggregate-documents')
        
      from('seda:aggregate-documents').
      aggregate(constant('null')).completionInterval(60000L).groupExchanges().
        processRef('docsAggregator').
        setHeader(Exchange.HTTP_URI, constant("${CH.config.gnutch.solr.serverUrl}/update?commit=true")). 
        setHeader(Exchange.HTTP_METHOD, constant('POST')).
        to("http://null")
    }
}
