import org.apache.camel.Exchange
import org.apache.xpath.XPathAPI
import java.text.SimpleDateFormat
import org.apache.camel.LoggingLevel

gnutch {
  // Input route definition 
  inputRoute = "file://${System.env.HOME}/.gnutch/input"


  aggregationTime = 60000L

  crawl {
    // Size of crawling thread pool
    threads = {-> java.lang.Runtime.getRuntime().availableProcessors() }()
    multiplier = 5
  }

  handlers {
    // org.apache.camel.Processor definition. Called after HTML is transformed into XHTML
    // ex.in.body contains XHML document (actually contains reference to org.w3c.org.Document)
    postHTTP = { Exchange ex ->

    }

    // org.apache.camel.Processor definition. Called after HTML is transformed into XHTML 
    // ex.in.body contains XHML document (actually contains reference to org.w3c.org.Document)
    postXHTML = { Exchange ex ->

    } 

    // org.apache.camel.Processor definition. Called after XML is built
    // ex.in.body contains XML document (actually contains reference to org.w3c.org.Document)
    postXML = { Exchange ex ->
    } 

    validate = { Exchange ex ->
    }

    // Route definition. Should consume from 'direct:publish' and provide some business logic
    publish = {
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
    brokerURL = 'vm://localhost?broker.persistent=false'
    // brokerURL = 'tcp://localhost:61616'
    // conf = 'classpath:activemq.xml'
  }   
}
