package gnutch.urls

import org.apache.camel.Exchange
import org.apache.camel.Processor

class ContextUrlResolver implements Processor {

  void process (Exchange exchange) {
    // contextURI always starts with http://
    def contextURI = new URL(unescape(exchange.in.headers['contextURI']))
    def contextBase = exchange.in.headers['contextBase']
    if(contextBase && contextBase.length) {
      contextBase = new URL(contextBase.item(0).nodeValue)
    }
    else {
      contextBase = null
    }

    def body = ContextUrlResolver.unescape(exchange.in.body.trim())
    URL url

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
    } else {
      url = new URL(
        contextURI.protocol,
        contextURI.host,
        contextURI.port,
        contextURI.path)

      url = new URL(url, body)
    }

    exchange.in.body = url.toString()
  }

  protected static String unescape(String str){
    def sw = new StringWriter()
    byte[] bytes = new byte[1024]
    int bytePtr = 0
    def chars = str.toCharArray()
    for(int i=0;i<chars.length;i++){
      assert bytePtr < bytes.length
      def ch = chars[i]
      if(ch == '%'){
        bytes[bytePtr++] = Integer.parseInt(str.substring(i+1, i+3), 16)
        i+=2
      } else {
        sw.append(new String(Arrays.copyOf(bytes, bytePtr), 'UTF-8'))
        bytePtr = 0

        sw.append(ch)
      }
    }
    sw.append(new String(Arrays.copyOf(bytes, bytePtr), 'UTF-8'))

    return sw.toString()
  }
}
