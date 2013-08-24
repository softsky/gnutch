class GnutchGrailsPlugin {

    def version = "0.2"
    def grailsVersion = "2.0.0 > *"
    def loadAfter = ['controllers', 'services', 'routing']
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]
    def title = "Grails Apache Nutch alternative"
    def author = "Arsen A. Gutsal"
    def authorEmail = "gutsal.arsen@gmail.com"
    def documentation = "http://grails.org/plugin/gnutch"
    def license = "GPL3"
    //    def developers = [ [ name: "", email: "" ]]
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

      jmsFactory(org.apache.activemq.ActiveMQConnectionFactory) {
        brokerURL = conf.gnutch.activemq.brokerURL
      }

      jmsConnectionFactory(org.apache.activemq.pool.PooledConnectionFactory) {
        connectionFactory = ref('jmsFactory')
      }

      http(org.apache.camel.component.http.HttpComponent){
        camelContext = ref('camelContext')
        httpConnectionManager = ref('httpConnectionManager')
        httpClientConfigurer = new gnutch.http.HttpClientConfigurer(conf.gnutch.http.userAgent)
      }

      httpConnectionManager(org.apache.commons.httpclient.MultiThreadedHttpConnectionManager){
        params = ref('httpConnectionManagerParams')
      }

      httpConnectionManagerParams(org.apache.commons.httpclient.params.HttpConnectionManagerParams){
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
   
    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
