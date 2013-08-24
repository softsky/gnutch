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
  // Input route definition 
  inputRoute = 'file:///home/archer/tmp/gnutch-input'

  crawl {
   // Crawling thread pool
   threads = 20
  }

  // post processors
  postProcessorHTML = {ex ->} 

  postProcessorXML = {ex ->} 
  
  http {
    // UserAgent string. Better if contain email address of person who is responsible 
    // for crawling. That will allow source owners to contact person directly
    userAgent = "GNutch crawler. Contact maintainer: admin@softsky.com.ua"
    // Maximmum number of connections per host
    defaultMaxConnectionsPerHost = 40
    // Maximmum number of total connections
    maxTotalConnections = 40
  }

  solr {
    // URL to Solr server
    serverUrl = 'http://vm4:8983/solr'
  }
 
  
  activemq {
    // URL to message broker
    brokerURL = 'vm://localhost'
    // conf = 'classpath:activemq.xml'
  } 

}