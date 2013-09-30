package gnutch

import org.apache.camel.Exchange

import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.Parser
import org.apache.tika.sax.BodyContentHandler

/**
   Extracts content form binary input stream
   and returns extracted textual string
   Is called from Camel route when we find that returned content is not text/html
 */
class TikaContentExtractor {
  /**
      Accepts binary InputStream, then use Apache Tika to extract content
      @param is - input stream to be parsed
      @return textual stream with extracted content
  */
  String extract(InputStream is, Exchange ex){
    try {
      StringWriter sw = new StringWriter()

      Parser parser = new AutoDetectParser()
      BodyContentHandler handler = new BodyContentHandler(sw)

      Metadata metadata = new Metadata()

      parser.parse(is, handler, metadata, new ParseContext())

      ex.in.headers.put('Tika-Metadata', metadata)

      return sw.toString()

    } finally {
      is.close()
      sw.close()
    }
  }
}
