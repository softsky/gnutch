import org.apache.camel.Exchange
import org.apache.xpath.XPathAPI
import java.text.SimpleDateFormat

gnutch {
  // Input route definition 
  inputRoute = 'file:///home/archer/tmp/gnutch-input'

  crawl {
   // Size of crawling thread pool
    threads = {-> java.lang.Runtime.getRuntime().availableProcessors() }()
  }

  handlers {
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
      def title = XPathAPI.eval(ex.in.body, "//field[@name = 'title']").str()
      def content = XPathAPI.eval(ex.in.body, "//field[@name = 'content']").str()

      return (title.equals("") == false) && (content.equals("") == false)
    }

    // Route definition. Should consume from 'direct:publish' and provide some business logic
    publish = {
       // This route will save files to a directory
      /*
       from('direct:publish').
       to("file:///home/archer/tmp/gnutch-output/")
      */


      // This route will commit to a solr server
      from('direct:publish').
      setHeader(Exchange.HTTP_URI, constant("http://softsky.com.ua/solr/collection1/update?commit=true")).
      setHeader(Exchange.HTTP_METHOD, constant('POST')).
      setHeader(Exchange.CONTENT_TYPE, constant('application/xml')).
      to("http://null")

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
  }

  solr {
    // URL to Solr server
    masterCoreUrl = 'http://softsky.com.ua/solr/collection1'
    slaveCoreUrl = 'http://softsky.com.ua/solr/collection1'
  }
 
  
  activemq {
    // URL to message broker
    brokerURL = 'vm://localhost'
    // brokerURL = 'tcp://localhost:61616'
    // conf = 'classpath:activemq.xml'
  } 
  
  support.email = 'sourcingtool-support@softsky.com.ua'
  validation.query = '-title:["" TO *]&group=true&group.field=source'

}
