package gnutch

import org.w3c.dom.Document
import org.apache.xpath.XPathAPI
import org.apache.camel.Exchange

import org.codehaus.groovy.grails.io.support.PathMatchingResourcePatternResolver

/**
   Service class for storing key:value pair
 */
final class Entry<K, V> implements Map.Entry<K, V> {
  private final K key;
  private V value;

  public Entry(K key, V value) {
    this.key = key;
    this.value = value;
  }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    public V setValue(V value) {
      V old = this.value;
      this.value = value;
      return old;
    }

    @Override
    public String toString(){
      return key + ":" + value;
    }
}

/**
   Method #collect is called every time we find the document which didn't pass validation 
   defined in Config.groovy gnutch.handlers.validate
*/
class InvalidDocumentCollectorService {

  def grailsApplication

  private Map<String, List<Entry<String, String>>> documents = new HashMap<String, List<Entry<String, String>>>()

  public void collect(Exchange ex){
    String result, source
    if(XPathAPI.eval(ex.in.body, "//field[@name = 'title']/text()").str().trim().equals("")){
      result = "title";
    }

    if(XPathAPI.eval(ex.in.body, "//field[@name = 'content']/text()").str().trim().equals("")){
      result = "content";
    }

    String invalidSource = XPathAPI.eval(ex.in.body, "//field[@name = 'source']/text()").str()


    def resources = new PathMatchingResourcePatternResolver().getResources("classpath:xslt/*.xsl")
    def reader
    resources.each { resource ->
      try {
        reader = new InputStreamReader(resource.inputStream)
        String ln;
        while((ln = reader.readLine()) != null){
          if (ln =~ "<gn:init>.*$invalidSource.*</gn:init>") {
            source = resource.filename
            break;
          }
        }

      } finally {
        reader.close();
      }
    }

    synchronized(documents){
      if(documents[source] == null)
        documents[source] = new ArrayList<String>()
        synchronized(documents[source]){
          documents[source] << new Entry<String, String>(result, ex.in.headers["contextURI"])
        }
    }
  }

  public Map<String, List<String>> getDocuments(){
    return Collections.unmodifiableMap(documents);
  }

}
