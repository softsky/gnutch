package gnutch.indexer

import org.apache.camel.Header
import org.apache.camel.Body

import java.util.Collections

import org.w3c.dom.Document

import org.springframework.core.io.ClassPathResource

import javax.xml.transform.dom.DOMSource
import javax.xml.transform.dom.DOMResult

import javax.xml.transform.stream.StreamSource
import javax.xml.transform.TransformerFactory
import javax.xml.transform.Transformer

import org.apache.commons.logging.LogFactory

import javax.xml.transform.stream.StreamResult

class DocumentIndexer {
  private static def log = LogFactory.getLog(this)
  /** Map of regexp/stylesheet values */
  final def Map<String, Document> transformations = Collections.synchronizedMap([:])
  private static final String TRANSFORMER_FACTORY_CLASS = "org.apache.xalan.processor.TransformerFactoryImpl";
  
  public Document index(@Header("contextURI") String contextURI, @Body Document body){
    log.trace "Testing ${contextURI}, ${body}"
    def matched = null
    def result 
    synchronized(transformations) {
    transformations.each { entry ->
      // looking for one that match
      if(contextURI.matches(entry.key)){
        if(matched) throw new RuntimeException("""contextURI:${contextURI} already matched ${matched} pattern\n
Now it also matches ${entry.key} pattern, which significantly drops perfromace\n
as trasnformation is executed couple of times. Please, check your transformations map.
""")
        matched = entry.key
        def domResult = new DOMResult()
        def tf = TransformerFactory.newInstance(TRANSFORMER_FACTORY_CLASS, null)
        def transformer = tf.newTransformer(new DOMSource(entry.value))

        if(transformer == null)
          throw new RuntimeException("XLT is seems invalid for this URI: ${contextURI}")

        transformer.setParameter('contextURI', contextURI)
        transformer.transform(new DOMSource(body), domResult)

        // if TRACE log level is enabled for gnutch.indexer.DocumentIndexer.dom
        // dumping input XHTML onto System.out
        if(LogFactory.getLog(this.getClass().name + ".dom").isTraceEnabled()){
          def t = tf.newTransformer()
          t.transform(new DOMSource(body), new StreamResult(System.out))
        }

 
        result = domResult.node
      }
    }
    }
    log.trace "${contextURI} ${result?' is indexable':' is not indexable'}"
    return result
  }

  public boolean isIndexable(@Header("contextURI") String contextURI){
    return transformations.keySet().any { contextURI.matches(it) }
  }

}