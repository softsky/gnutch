package gnutch

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.springframework.core.io.ClassPathResource

class SourceUrlsProducerService {

    static transactional = true

    def produce() {
      new ClassPathResource(CH.config.gnutch.sourceUrls).inputStream.eachLine { line -> 
        println line
        sendMessage('activemq:input-url', line)
      }
    }
}
