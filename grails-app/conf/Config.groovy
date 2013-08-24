// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}

gnutch {
  // Define local directory which is used for receiving source-definition files.
  inputRoute = 'file:///tmp/gnutch-input'

  crawl {
    // Define fixed number of threads we use for crawling
    threads = 40
  }

  // post processors
  // define custom post processing closure which is called for HTML pages
  // ex.in.body contains org.w3c.dom.Document object represending XHTML page just crawled
  postProcessorHTML = {ex ->}

  // define custom post processing closure which is called for XML document which was received after XSLT
  // ex.in.body contains org.w3c.dom.Document object represending XML result of XSL transformation
  postProcessorXML = {ex ->}


  http {
    // UserAgent string. Better if contain email address of person who is responsible 
    // for crawling. That will allow source owners to contact person directly
    userAgent = 'GNutch crawler (https://github.com/softsky/gnutch/). Contact: yourname@domain.com'

    // Since we use shared threadPool for HTTP connections,
    // we define these two values

    // Maximmum number of connections per host
    defaultMaxConnectionsPerHost = 40
    // Maximmum number of total connections
    maxTotalConnections = 40
  }

  solr {
    // URL to Solr server where we send crawled data for indexing
    serverUrl = 'http://localhost:8983/solr'
  }
 
  
  activemq {
    // URL to AMQ message broker. 
    // For simple configuration it runs embedded AMQ
    // More on possible connection strings here: http://activemq.apache.org/configuring-transports.html
    // brokerURL = 'tcp://0.0.0.0:61616'
    brokerURL = 'vm://localhost'

    // For more complex cases it could contain external AMQ configuration
    //conf = 'classpath:activemq.xml'
  } 
}
