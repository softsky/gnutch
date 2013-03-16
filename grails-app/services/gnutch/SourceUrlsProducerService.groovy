package gnutch

import org.springframework.beans.factory.annotation.Autowired

import javax.xml.xpath.*
import javax.xml.namespace.NamespaceContext

import gnutch.indexer.DocumentIndexer
import gnutch.urls.RegexUrlChecker

class SourceUrlsProducerService {

  static transactional = true

  @Autowired
  def RegexUrlChecker regexUrlChecker

  def produce(def doc) {
    def xpath = XPathFactory.newInstance().newXPath()
    // defining namespace context
    def nsContext = [
      getNamespaceURI: { prefix -> "https://github.com/softsky/gnutch" },
      getPrefix: { uri -> "gn" },
      getPrefixes: { uri -> ["gn"].iterator() } ,
    ] as NamespaceContext

    xpath.setNamespaceContext(nsContext)
    // extracting values for init, patter and urlfilter 
    def init = xpath.evaluate('gn:init/text()', doc.documentElement)
    def pattern = xpath.evaluate('gn:pattern/text()', doc.documentElement)
    def urlfilter = xpath.evaluate('gn:urlfilter/text()', doc.documentElement)

    // making sure all set
    assert init && patter && urlfiler
      
    // adding document into map of pattern:document
    DocumentIndexer.transformations.put(pattern, doc) 
  
    // applying urlfilter
    urlfilter.split('\n').each { line ->
      line = line.trim()
      if(line != "")
        switch(line.charAt(0)){
          case '+':regexUrlChecker.allowedPatternList << line.substring(1);break;
          case '-':regexUrlChecker.ignorePatternList << line.substring(1);break;
          default: throw new RuntimeException("urlfilter entry should start with +/-");break;
        }
    }
  
    // sending init url into the queue for crawling
    sendMessage('activemq:input-url', init)
  }
}
