package gnutch

import grails.test.mixin.TestFor

import javax.xml.parsers.DocumentBuilderFactory
import org.apache.xpath.XPathAPI
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultCamelContext

import java.util.concurrent.TimeUnit
import java.util.concurrent.Executors

import org.codehaus.groovy.grails.io.support.PathMatchingResourcePatternResolver

import grails.test.GrailsUnitTestCase

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
class InvalidDocumentCollectorServiceTests extends GrailsUnitTestCase {
  def service
  def rnd
  
  @Before
  void setUp(){
    service = new InvalidDocumentCollectorService()
    rnd = new Random()
  }

  @Test
  void testCollect() {
    assert service
    def ALPHABET = 'abcdefgh'

    // Create fixed thread pool according to the number of available processors
    def ncpus = java.lang.Runtime.getRuntime().availableProcessors()
    def executor = Executors.newFixedThreadPool(ncpus)

    def resources = new PathMatchingResourcePatternResolver().getResources("classpath:xslt/*.xsl")
    def r = { ->
      def reader
      def url = null
      resources.each { resource ->
        if(rnd.nextInt(50) <= 2){
          try {
            reader = new InputStreamReader(resource.inputStream)
            String ln;
            while((ln = reader.readLine()?.trim()) != null){
              if (ln.startsWith('<gn:init>')) {
                url = "http://" + (ln =~ /<gn:init>https?:\/\/([^\/]*\/?)(.*)?<\/gn:init>/)[0][1]
                break;
              }
            }
          } finally {
            reader.close();
          }
        }
      }

      // FIXME this is hardcoded hack
      if(url == null){
        url = 'http://www.newswire.ca'
      }

      def source = url

      url += ALPHABET.collect{
        it += rnd.nextBoolean()?'/':''
      }.join()

      def writer = new StringWriter()
      def xml = new groovy.xml.MarkupBuilder(writer)
      xml.doc(){
        field(name:'id', url)
        field(name:'title', ALPHABET.collect { rnd.nextBoolean()?it:''}.join())
        field(name:'content', ALPHABET.collect { rnd.nextBoolean()?it:''}.join())
        field(name:'source', (source =~ /http:\/\/(.*)/)[0][1])
      };

      def db = DocumentBuilderFactory.newInstance().newDocumentBuilder()
      def xmlStr = writer.toString()
      def doc = db.parse(new ByteArrayInputStream(xmlStr.getBytes()))

      if(rnd.nextBoolean()){
        XPathAPI.selectSingleNode(doc, "//field[@name = 'title']/text()")?.setNodeValue('')
      } else {
        XPathAPI.selectSingleNode(doc, "//field[@name = 'content']/text()")?.setNodeValue('')
      }

      def camelContext = new DefaultCamelContext()
      def ex = new DefaultExchange(camelContext)

      ex.in.body = doc
      ex.in.headers['contextURI'] = url
      service.collect(ex)
    } as Runnable

    def ncount = 1000
    ncount.times {
      executor.execute(r)
    }
    executor.shutdown()
    executor.awaitTermination(60, TimeUnit.SECONDS)

    println service.documents
    assert service.documents.size() <= ncount
    assert service.documents.entrySet().sum { it.value.size() } == ncount
  }
}
