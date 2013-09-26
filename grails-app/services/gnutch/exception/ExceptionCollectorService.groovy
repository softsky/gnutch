package gnutch.exception

import org.apache.camel.Exchange
import grails.plugin.mail.MailService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn

@DependsOn("camelContext")
class ExceptionCollectorService {

  def grailsApplication
  private static Map<Class<? extends Throwable>, Map<String, Throwable>> exceptions = 
               new HashMap<Class<? extends Throwable>, Map<String, Throwable>>()

  public <T extends Throwable> void collectException(Exchange exchange) {
    def Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT)
    if(exception){
      synchronized(exceptions){
        Class<T> key = exception.getClass()
        if(exceptions.get(key) == null)
          exceptions.put(key, new HashMap<String, Throwable>());
        
          exceptions.get(key).put(exchange.in.headers.contextURI, exception)
      }
    }
  }

  public def getExceptions(){
    return exceptions
  }
}
