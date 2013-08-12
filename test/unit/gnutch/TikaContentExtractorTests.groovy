package gnutch

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.io.support.FileSystemResource

import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.impl.DefaultCamelContext


/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class TikaContentExtractorTests {

  def contentExtractor
  def exchange

    void setUp() {
        contentExtractor = new TikaContentExtractor()

        def defaultCamelContext = new DefaultCamelContext()
        exchange = new DefaultExchange(defaultCamelContext)
    }

    void tearDown() {
        // Tear down logic here
    }

    void testExtract() {
        String result

        //pdf extraction----------------------------------------------------------------------
        def is = new FileSystemResource('test/unit/resources/gnutch/help.pdf').inputStream
        result = contentExtractor.extract(is, exchange)

        assert result
        assert result.contains('Prepare for profiling')

        assert exchange.in.headers['Tika-Metadata']
        assert exchange.in.headers['Tika-Metadata']['title'] == 'Untitled'
        //----------------------------------------------------------------------

        //excel file extraction----------------------------------------------------------------------
        is = new FileSystemResource('test/unit/resources/gnutch/Document extracted.xlsx').inputStream
        result = contentExtractor.extract(is, exchange)


        assert result
        assert result.contains('Document extracted')

        assert exchange.in.headers['Tika-Metadata']
        assert exchange.in.headers['Tika-Metadata']['title'] == 'Document extracted'
        //----------------------------------------------------------------------

        //word file extraction----------------------------------------------------------------------
        is = new FileSystemResource('test/unit/resources/gnutch/Document extracted.docx').inputStream
        result = contentExtractor.extract(is, exchange)

        assert result
        assert result.contains('Document extracted')

        assert exchange.in.headers['Tika-Metadata']
        assert exchange.in.headers['Tika-Metadata']['title'] == 'Document extracted'
        //----------------------------------------------------------------------

        //power point file extraction----------------------------------------------------------------------
        is = new FileSystemResource('test/unit/resources/gnutch/Document extracted.pptx').inputStream
        result = contentExtractor.extract(is, exchange)

        assert result
        assert result.contains('Document extracted')

        assert exchange.in.headers['Tika-Metadata']
        assert exchange.in.headers['Tika-Metadata']['title'] == 'Document extracted'
        //----------------------------------------------------------------------

        //open office file extraction----------------------------------------------------------------------
        is = new FileSystemResource('test/unit/resources/gnutch/Document extracted.odt').inputStream
        result = contentExtractor.extract(is, exchange)

        assert result
        assert result.contains('Document extracted')

        assert exchange.in.headers['Tika-Metadata']
        assert exchange.in.headers['Tika-Metadata']['title'] == 'Document extracted'
        //----------------------------------------------------------------------

    }
}
