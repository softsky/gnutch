package gnutch

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class SourceUrlsProducerService {

    static transactional = true

    def produce(def file) {
      // decorating with Groovy File in order to access inputStream
      new File(file.absolutePath).newInputStream().eachLine { line ->  
        println line
        sendMessage('activemq:input-url', line)
      }
    }
}
