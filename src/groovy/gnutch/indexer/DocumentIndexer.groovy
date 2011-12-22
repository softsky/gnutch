package gnutch.indexer

import org.apache.camel.Header
import org.apache.camel.Body

import org.w3c.dom.Document

import org.springframework.core.io.ClassPathResource

import javax.xml.transform.dom.DOMSource
import javax.xml.transform.dom.DOMResult
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.TransformerFactory
import javax.xml.transform.Transformer

import org.apache.commons.logging.LogFactory


class DocumentIndexer {
  private static def log = LogFactory.getLog(this)
  /** Map of regexp/stylesheet values */
  public static Map<String, String> transformations = [:]

  
  public Document index(@Header("contextURI") String contextURI, @Body Document body){
    log.trace "Testing ${contextURI}, ${body}"
    def matched = null
    def result 
    transformations.each { entry ->
      // looking for one that match
      if(contextURI.matches(entry.key)){
        if(matched) throw new RuntimeException("""contextURI:${contextURI} already matched ${matched} pattern\n
Now it also matches ${entry.key} pattern, which significantly drops perfromace\n
as trasnformation is executed couple of times. Please, check your transformations map.
""")
        matched = entry.key
        def domResult = new DOMResult()
        def tf = TransformerFactory.newInstance()
        def transformer = tf.newTransformer(new StreamSource(new ClassPathResource(entry.value).inputStream))
        transformer.setParameter('contextURI', contextURI)
        transformer.transform(new DOMSource(body), domResult)
        result = domResult.node
      }
    }
    log.trace "${contextURI} ${result?' is indexable':' is not indexable'}"
    return result
  }

  public boolean isIndexable(@Header("contextURI") String contextURI){
    return transformations.keySet().any { contextURI.matches(it) }
  }

}