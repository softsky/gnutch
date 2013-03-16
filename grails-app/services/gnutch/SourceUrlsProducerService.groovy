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
    // decorating with Groovy File in order to access inputStream
      
    def nsContext = [
      getNamespaceURI: { prefix -> "https://github.com/softsky/gnutch" },
      getPrefix: { uri -> "gn" },
      getPrefixes: { uri -> ["gn"].iterator() } ,
    ] as NamespaceContext

    xpath.setNamespaceContext(nsContext)
    def init = xpath.evaluate('gn:init/text()', doc.documentElement)
    def pattern = xpath.evaluate('gn:pattern/text()', doc.documentElement)
    def urlfilter = xpath.evaluate('gn:urlfilter/text()', doc.documentElement)
      
    DocumentIndexer.transformations.put(pattern, doc) // adding document into map of pattern:document
  
    urlfilter.split('\n').each { line ->
      line = line.trim()
      if(line != "")
        switch(line.charAt(0)){
          case '+':regexUrlChecker.allowedPatternList << line.substring(1);break;
          case '-':regexUrlChecker.ignorePatternList << line.substring(1);break;
          default: throw new RuntimeException("urlfilter entry should start with +/-");break;
        }
    }
  
    sendMessage('activemq:input-url', init)
      
    /*
      new File(file.absolutePath).newInputStream().eachLine { line ->  
        if(line.trim().equals("") == false)
          sendMessage('activemq:input-url', line)
      }
      */
    }
}
