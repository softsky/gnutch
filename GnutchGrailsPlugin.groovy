import gnutch.http.HttpClientConfigurer

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.pool.PooledConnectionFactory
import org.apache.camel.component.http.HttpComponent
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
import org.apache.commons.httpclient.params.HttpConnectionManagerParams

class GnutchGrailsPlugin {

    def version = "0.2.1"
    def grailsVersion = "2.0.0 > *"
    def loadAfter = ['controllers', 'services', 'routing']
    def title = "Grails Apache Nutch alternative"

    def documentation = "http://grails.org/plugin/gnutch"

    def license = "APACHE"
    def developers = [
      [name: "Arsen A. Gutsal", email: "gutsal.arsen@gmail.com"]
    ]
    def issueManagement = [ system: "GitHub", url: "https://github.com/softsky/gnutch/issues" ]
    def scm = [ url: "https://github.com/softsky/gnutch" ]
    def description = '''\
Very simple alternative to "Apache Nutch":http://nutch.apache.org/ created in Grails.

Crawled data could be stored to files, saved to database or sent to Apache Solr server for indexing.
Use "Apache Camel":http://camel.apache.org/ as integration framework and "Apache ActiveMQ":http://activemq.apache.org/ as source messaging and integration patterns server.
'''

    def doWithSpring = {

      def conf = application.config

      if(conf.gnutch.activemq?.conf){
        println "Importing activemq configuration from ${conf.gnutch.activemq.conf}"
        importBeans conf.gnutch.activemq.conf
      }

      jmsFactory(ActiveMQConnectionFactory) {
        brokerURL = conf.gnutch.activemq.brokerURL
      }

      jmsConnectionFactory(PooledConnectionFactory) {
        connectionFactory = ref('jmsFactory')
      }

      http(HttpComponent){
        camelContext = ref('camelContext')
        httpConnectionManager = ref('httpConnectionManager')
        httpClientConfigurer = new HttpClientConfigurer(conf.gnutch.http.userAgent)
      }

      httpConnectionManager(MultiThreadedHttpConnectionManager){
        params = ref('httpConnectionManagerParams')
      }

      httpConnectionManagerParams(HttpConnectionManagerParams){
        defaultMaxConnectionsPerHost = conf.gnutch.http.defaultMaxConnectionsPerHost
        maxTotalConnections = conf.gnutch.http.maxTotalConnections
      }

      docsAggregator(gnutch.processors.DocsAggregator)

      regexUrlChecker(gnutch.urls.RegexUrlChecker)

      contextUrlResolver(gnutch.urls.ContextUrlResolver)

      documentIndexer(gnutch.indexer.DocumentIndexer)

      schedulerService(gnutch.quartz.SchedulerService)

      tikaContentExtractor(gnutch.TikaContentExtractor)
    }
}
