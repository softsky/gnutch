import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

import org.apache.commons.httpclient.HttpVersion
import org.apache.commons.httpclient.params.HttpClientParams
import org.apache.commons.httpclient.Cookie
import org.apache.commons.httpclient.HttpState

// Place your Spring DSL code here
beans = {
  if(CH.config.gnutch.activemq?.conf){
    println "Importing activemq configuration from ${CH.config.gnutch.activemq.conf}"
    importBeans CH.config.gnutch.activemq.conf
  }

  jmsFactory(org.apache.activemq.ActiveMQConnectionFactory) {
    brokerURL = CH.config.gnutch.activemq.brokerURL
  }

  jmsConnectionFactory(org.apache.activemq.pool.PooledConnectionFactory) {
    connectionFactory = ref('jmsFactory')
  }


  http(org.apache.camel.component.http.HttpComponent){
    camelContext = ref('camelContext')
    httpConnectionManager = ref('httpConnectionManager')
    httpClientConfigurer = new org.apache.camel.component.http.HttpClientConfigurer(){
      @Override
      void configureHttpClient(org.apache.commons.httpclient.HttpClient client){
        client.params.setParameter(HttpClientParams.PROTOCOL_VERSION, HttpVersion.HTTP_1_1)
        if(CH.config.gnutch.httpclient?.userAgent)
          client.params.setParameter(HttpClientParams.USER_AGENT, CH.config.gnutch.http?.userAgent)
      }
    }
  }

  httpConnectionManager(org.apache.commons.httpclient.MultiThreadedHttpConnectionManager){
    params = ref('httpConnectionManagerParams')
  }

  httpConnectionManagerParams(org.apache.commons.httpclient.params.HttpConnectionManagerParams){
    defaultMaxConnectionsPerHost = CH.config.gnutch.http.defaultMaxConnectionsPerHost
    maxTotalConnections = CH.config.gnutch.http.maxTotalConnections
  }

  docsAggregator(gnutch.processors.DocsAggregator)

  regexUrlChecker(gnutch.urls.RegexUrlChecker)

  contextUrlResolver(gnutch.urls.ContextUrlResolver)

  documentIndexer(gnutch.indexer.DocumentIndexer)

  schedulerService(gnutch.quartz.SchedulerService)

  tikaContentExtractor(gnutch.TikaContentExtractor)

}
