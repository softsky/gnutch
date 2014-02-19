import org.apache.camel.Exchange
import org.apache.xpath.XPathAPI
import java.text.SimpleDateFormat
import org.apache.camel.LoggingLevel

gnutch {
  // Input route definition
  inputRoute = 'file:///home/archer/tmp/gnutch-input'

  aggregationTime = 60000L

  crawl {
    // Size of crawling thread pool
    threads = {-> java.lang.Runtime.getRuntime().availableProcessors() }()
    multiplier = 10
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
      // aquiring `date` field
      def doc = ex.in.body
      def dateNode = doc.createElement("field")
      dateNode.setAttribute("name", "date")
      def format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
      dateNode.appendChild(doc.createTextNode(format.format(new Date())))
      doc.firstChild.appendChild(dateNode)

      // aquiring `source` field
      def nodeList = doc.getElementsByTagName("field")
      def id = nodeList.find { it.getAttribute("name") == "id"}.textContent

      def m = id =~ /https?:\/\/([^\/]*)\//
      try{
        def sourceElement = doc.createElement("field")
        sourceElement.setAttribute("name", "source")
        sourceElement.appendChild(doc.createTextNode(m[0][1]))
        doc.firstChild.appendChild(sourceElement)
      }catch(Exception e){
        println id + " no match found"
      }
    }

    validate = { Exchange ex ->
      return true
      def title = XPathAPI.eval(ex.in.body, "//field[@name = 'title']").str()
      def content = XPathAPI.eval(ex.in.body, "//field[@name = 'content']").str()

      return (title.trim().equals("") == false) && (content.trim().equals("") == false)
    }

    // Route definition. Should consume from 'direct:publish' and provide some business logic
    publish = {
      from('direct:publish').
      setHeader(Exchange.HTTP_QUERY, constant('commit=true')).
      setHeader(Exchange.HTTP_METHOD, constant('POST')).
      setHeader(Exchange.CONTENT_TYPE, constant('application/xml')).
      log(LoggingLevel.INFO, 'gnutch', 'Headers: ${headers}, Body: ${body}').
      to("${gnutch.solr.coreUrl}/update")
    }
  }

  http {
    // UserAgent string. Better if contain email address of person who is responsible
    // for crawling. That will allow source owners to contact person directly
    userAgent = "GNutch crawler. Contact maintainer: admin@softsky.com.ua"
    // Maximmum number of connections per host
    defaultMaxConnectionsPerHost = 1000
    // Maximmum number of total connections
    maxTotalConnections = 1000
    customHeaders = []
  }

  solr {
    coreUrl = 'http://217.196.165.81:8983/solr/collection2'
  }

  activemq {
    // URL to message broker
    //brokerURL = 'vm://localhost'
    brokerURL = 'tcp://localhost:61616'
    // conf = 'classpath:activemq.xml'
  }
}

environments {
  productoin {
    log4j = {
      error  'org.codehaus.groovy.grails',
             'org.springframework',
             'org.hibernate',
             'net.sf.ehcache.hibernate'

      warn  'org.apache.camel'
      info 'gnutch'
    }

    gnutch {
      solr {
        coreUrl = 'http://localhost:8986/solr/collection2'
      }

      activemq {
        brokerURL = 'tcp://localhost:61616'
      }
    }
  }

  development {
    log4j = {
      error  'org.codehaus.groovy.grails',
             'org.springframework',
             'org.hibernate',
             'net.sf.ehcache.hibernate'

      warn  'org.apache.camel'
      debug 'gnutch'
    }

    gnutch.handlers.publish = {
      from('direct:publish').
      //to("file:///home/archer/tmp/gnutch-output")
      setHeader(Exchange.HTTP_URI, constant("${gnutch.solr.coreUrl}/update")).
      setHeader(Exchange.HTTP_QUERY, constant('commit=true')).
      setHeader(Exchange.HTTP_METHOD, constant('POST')).
      setHeader(Exchange.CONTENT_TYPE, constant('application/xml')).
      to("http4://null")
    }

    gnutch.http.customHeaders = [
      [Cookie: 'PHPSESSID=ee4dcb36618f7895e38c324b53c1c4d5; PHPSESSID=4db6b6c7b52976d3374d8ee98b4bf411; b=b; __utma=17636961.610814665.1392452407.1392452407.1392496920.2; __utmc=17636961; __utmz=17636961.1392452407.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)']
      ]

    gnutch.solr.coreUrl = 'http://217.196.165.81:8983/solr/collection2'

    gnutch.activemq.brokerURL = 'vm://localhost'
  }

  test {
    log4j = {
      error  'org.codehaus.groovy.grails',
             'org.springframework',
             'org.hibernate',
             'net.sf.ehcache.hibernate'

      debug  'org.apache.camel'
      trace  'gnutch'
    }

    gnutch.aggregationTime = 15000L

    gnutch.crawl.threads = {-> java.lang.Runtime.getRuntime().availableProcessors() }()
    gnutch.crawl.multiplier = 5

    gnutch.handlers.publish = {
      from('direct:publish').
      log(LoggingLevel.DEBUG, 'gnutch', 'Committed')
    }

    gnutch.activemq.brokerURL = 'vm://localhost?broker.persistent=false'

  }
}

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
