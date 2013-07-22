package gnutch.urls
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.util.UnsafeUriCharactersEncoder

class ContextUrlResolver implements Processor {
  public static String replaceLast(String text, String regex, String replacement) {
    return text.replaceFirst("(?s)"+regex+"(?!.*?"+regex+")", replacement);
  }

  public void process (Exchange exchange) {
    // contextURI always starts with http://
    def contextURI = new URI(exchange.in.headers['contextURI'])
    def contextBase = exchange.in.headers['contextBase']
    if(contextBase && contextBase.length)
      contextBase = new URI(contextBase.item(0).nodeValue)
      else
      contextBase = null

    def body = UnsafeUriCharactersEncoder.encode(exchange.in.body)

    if(body.toLowerCase().startsWith('javascript:')
       || body.toLowerCase().startsWith('mailto:')
       || body.toLowerCase().startsWith('tel:')) {
      contextURI = body // just do nothing, such URLs will be removed
    } else if(body.startsWith('?')){
      def uri = contextURI
      contextURI = new URI(uri.scheme, uri.userInfo, uri.host, uri.port, uri.path, body.substring(1), uri.fragment).toURL()
      // do nothing, contextURI does not change
    } else if(body.startsWith('#')){
      def uri = contextURI
      def fragment = null
      if(body != '#')
        fragment = body.substring(1)

      contextURI = new URI(uri.scheme, uri.userInfo, uri.host, uri.port, uri.path, uri.query, fragment).toURL()
      // do nothing, contextURI does not change
    }  else {
      try{
        if(contextBase)
          contextURI  = contextBase.resolve(URI.create(body)).toURL()
          else
          contextURI  = contextURI.resolve(URI.create(body)).toURL()
      }
      catch(MalformedURLException muex){
        if(muex.message.contains("unknown protocol")){
          contextURI = body
        } else {
          throw muex;
        }
      }
      catch(IllegalArgumentException iaex){
        contextURI = body
      }
    }

    exchange.in.body = contextURI.toString()
  }
}