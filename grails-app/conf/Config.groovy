import org.apache.camel.Exchange
import org.apache.xpath.XPathAPI
import java.text.SimpleDateFormat
import org.apache.camel.LoggingLevel

log4j = {
    error 'org.codehaus.groovy.grails',
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache.hibernate'
          

   warn  'org.apache.camel'
   trace  'gnutch'

   //trace 'gnutch.indexer.DocumentIndexer.dom' 
}


environments {
  test {

    gnutch {
      // Input route definition 
      inputRoute = 'file:///home/archer/tmp/gnutch-input'

      aggregationTime = 10000L

      crawl {
        // Size of crawling thread pool
        threads = {-> java.lang.Runtime.getRuntime().availableProcessors() }()
        multiplier = 5
      }

      handlers {
        // org.apache.camel.Processor definition. Called after HTML is transformed into XHTML 
        // ex.in.body contains XHML document (actually contains reference to org.w3c.org.Document)
        postXHTML = { Exchange ex ->

        } 

        // org.apache.camel.Processor definition. Called after XML is built
        // ex.in.body contains XML document (actually contains reference to org.w3c.org.Document)
        postXML = { Exchange ex ->

        } 

        validate = { Exchange ex ->
          return true
        }

        // Route definition. Should consume from 'direct:publish' and provide some business logic
        publish = {
          from('direct:publish').
          log(LoggingLevel.DEBUG, 'gnutch', 'Committed')
        }

      }
  
      http {
        // UserAgent string. Better if contain email address of person who is responsible 
        // for crawling. That will allow source owners to contact person directly
        userAgent = "GNutch crawler (https://github.com/softsky/gnutch): admin@gnutch.org"
        // Maximmum number of connections per host
        defaultMaxConnectionsPerHost = 1000
        // Maximmum number of total connections
        maxTotalConnections = 1000
      }
  
      activemq {
        // URL to message broker
        // brokerURL = 'vm://localhost'
        brokerURL = 'tcp://localhost:61616'
        // conf = 'classpath:activemq.xml'
      }   
    }
    
  }
}
