import gnutch.http.HttpClientConfigurer

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.pool.PooledConnectionFactory
import org.apache.camel.component.http4.HttpComponent
import org.apache.camel.Exchange

class GnutchGrailsPlugin {

    def version = "0.2.2.64"
    def grailsVersion = "2.2 > *"
    def loadAfter = ['controllers', 'services', 'routing']
    def title = "Grails Apache Nutch alternative"

    def documentation = "http://grails.org/plugin/gnutch"

    def license = "APACHE"
    def developers = [
            [name: "Arsen A. Gutsal", email: "gutsal.arsen@gmail.com"]
    ]
    def issueManagement = [system: "GitHub", url: "https://github.com/softsky/gnutch/issues"]
    def scm = [url: "https://github.com/softsky/gnutch"]
    def description = '''\
Very simple alternative to "Apache Nutch":http://nutch.apache.org/ created in Grails.

Crawled data could be stored to files, saved to database or sent to Apache Solr server for indexing.
Use "Apache Camel":http://camel.apache.org/ as integration framework and "Apache ActiveMQ":http://activemq.apache.org/ as source messaging and integration patterns server.
'''

    def doWithSpring = {

        // Defaulting config
        def conf = application.config.gnutch ?: [:]

        println "Config:" + conf

        conf.inputRoute = conf.inputRoute ?: 'file:///home/archer/tmp/gnutch-input'

        conf.aggregationTime = conf.aggregationTime ?: 30000L
        conf.crawl = conf.crawl ?: [threads: 1, multiplier: 1]

        conf.handlers = conf.handlers ?: [
                postXHTML: { Exchange ex -> },
                postXML: { Exchange ex -> },
                validate: { Exchange ex -> },
                publish: {}
        ]
        conf.http = conf.http ?: [
                // UserAgent string. Better if contain email address of person who is responsible
                // for crawling. That will allow source owners to contact person directly
                userAgent: 'GNutch crawler (https://github.com/softsky/gnutch): admin@gnutch.org',
                // Maximmum number of connections per host
                defaultMaxConnectionsPerHost: 1000,
                // Maximmum number of total connections
                maxTotalConnections: 1000,
        ]

        conf.activemq = conf.activemq ?: [
                // URL to message broker
                brokerURL: 'vm://localhost'
                // brokerURL: 'tcp://localhost:61616'
                // conf: 'classpath:activemq.xml'
        ]

        if (conf.activemq.conf) {
            println "Importing activemq configuration from ${conf.activemq.conf}"
            importBeans conf.activemq.conf
        }

        jmsFactory(ActiveMQConnectionFactory) {
            brokerURL = conf.activemq.brokerURL
        }

        jmsConnectionFactory(PooledConnectionFactory) {
            connectionFactory = ref('jmsFactory')
            maxConnections = 8;
            //maximumActive = 500;
        }

        http4(HttpComponent) {
            camelContext = ref('camelContext')
            connectionsPerRoute = conf.http.defaultMaxConnectionsPerHost
            maxTotalConnections = conf.http.maxTotalConnections
            httpClientConfigurer = new HttpClientConfigurer(conf.http.userAgent)
        }

        docsAggregator(gnutch.processors.DocsAggregator)

        patternService(gnutch.urls.PatternService) { bean ->
            bean.scope = 'singleton' // explicitly setting scope to `singleton`
        }

        regexUrlChecker(gnutch.urls.RegexUrlChecker) { bean ->
            bean.scope = 'prototype'
            bean.factoryMethod = 'getInstance'
        }

        contextUrlResolver(gnutch.urls.ContextUrlResolver)

        documentIndexer(gnutch.indexer.DocumentIndexer)

        schedulerService(gnutch.quartz.SchedulerService)

        tikaContentExtractor(gnutch.TikaContentExtractor)
    }
}
