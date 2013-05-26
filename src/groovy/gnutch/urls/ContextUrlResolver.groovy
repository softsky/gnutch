package gnutch.urls

import org.apache.camel.Exchange
import org.apache.camel.Processor

import org.apache.commons.httpclient.util.URIUtil 

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

    def body = exchange.in.body.trim()
    if(body.toLowerCase().startsWith('javascript:') || body.toLowerCase().startsWith('mailto:') ){
      contextURI = body // just do nothing, such URLs will be removed via regex-urlfilter
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
    } else {
      if(contextBase)
        contextURI  = contextBase.resolve(URI.create(body)).toURL()
        else
        contextURI  = contextURI.resolve(URI.create(body)).toURL()
    }
    exchange.in.body = contextURI.toString()
  }
}