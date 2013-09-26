package gnutch.urls
import gnutch.UrlEscaper
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.util.UnsafeUriCharactersEncoder

class ContextUrlResolver implements Processor {
  public void process (Exchange exchange) {

    // contextURI always starts with http://
    def contextURI = new URL(UrlEscaper.unescape(exchange.in.headers['contextURI']))
    def contextBase = exchange.in.headers['contextBase']
    if(contextBase && contextBase.length)
      contextBase = new URL(contextBase.item(0).nodeValue)
      else
      contextBase = null

      def body = UrlEscaper.unescape(exchange.in.body.trim())
      println "============== body: ${body}"
      def URL url

      // FIXME: this is a dirty hack
      if(body.toLowerCase().startsWith('javascript:') 
         || body.toLowerCase().startsWith('mailto:') 
         || body.toLowerCase().startsWith('tel:')){
        exchange.in.body = null
        return
      } 

      if(body.startsWith('http://') || body.startsWith('https://') || body.startsWith('ftp://')){
        url = new URL(body)
      } else if(body.startsWith('/')) {
        url = new URL(
          contextURI.protocol, 
          contextURI.host, 
          contextURI.port, 
          body)
      } else if(body.startsWith("?")){
        url = new URL(
          contextURI.protocol, 
          contextURI.host, 
          contextURI.port, 
          contextURI.path + body)
      } else if(body.startsWith("#")){
        url = new URL(
          contextURI.protocol, 
          contextURI.host, 
          contextURI.port, 
          contextURI.path + (contextURI.query?:"") + body)
      }else {
        url = new URL(
          contextURI.protocol, 
          contextURI.host, 
          contextURI.port, 
          contextURI.path)

        url = new URL(url, body)
      }

      exchange.in.body = url.toString()
  }

}