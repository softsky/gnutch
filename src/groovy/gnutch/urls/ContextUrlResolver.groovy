package gnutch.urls
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.util.UnsafeUriCharactersEncoder

class ContextUrlResolver implements Processor {
  public void process (Exchange exchange) {
    // contextURI always starts with http://
    def contextURI = new URL(unescape(exchange.in.headers['contextURI']))
    def contextBase = exchange.in.headers['contextBase']
    if(contextBase && contextBase.length)
      contextBase = new URL(contextBase.item(0).nodeValue)
      else
      contextBase = null

      def body = unescape(exchange.in.body.trim())
      def URL url

      // FIXME: this is a dirty hack
      if(body.startsWith('javascript:') || body.startsWith('mailto:') || body.startsWith('tel:')){
        exchange.in.body = body
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

        url = new URI(
          url.protocol, 
          url.userInfo,
          url.host, 
          url.port, 
          url.path, 
          url.query,
          url.ref
        ).resolve(body).toURL()
      }

      contextURI = new URI(
        url.protocol, 
        url.userInfo,
        url.host, 
        url.port, 
        url.path,
        url.query,
        url.ref
      )
         
      exchange.in.body = contextURI.toASCIIString()
  }

  private String unescape(String str){
    str.replaceAll(/%[A-Z,0-9]{2}/,{ return (char)Integer.parseInt(it.substring(1), 16) })
  }
}