package gnutch.urls

import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultExchange
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.Processor

import grails.test.*


class ContextUrlResolverTests extends GrailsUnitTestCase {
  def processor
    protected void setUp() {
        super.setUp()
        processor = new ContextUrlResolver()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testReplaceLast() {
      assert ContextUrlResolver.replaceLast('http://ziprealty.com/detailed', '/detailed', '/map') == 'http://ziprealty.com/map'
      assert ContextUrlResolver.replaceLast('http://ziprealty.com/detailed', '/\\w*', '/map') == 'http://ziprealty.com/map'
      assert ContextUrlResolver.replaceLast('http://ziprealty.com/./detailed', '/\\./\\w*', '/map') == 'http://ziprealty.com/map'
    }
    void testProcess() {
      def ctx = new DefaultCamelContext()
      def ex = new DefaultExchange(ctx)

      ex.in.headers['contextURI'] = 'http://www.google.com/a/b/c'
      ex.in.body = 'http://www.z.com'
      processor.process(ex)
      assert ex.in.body == 'http://www.z.com'

      ex.in.headers['contextURI'] = 'http://www.google.com/a/b/c'
      ex.in.body = 'javascript:void(0)'
      processor.process(ex)
      assert ex.in.body == 'javascript:void(0)'

      ex.in.headers['contextURI'] = 'http://www.google.com/a/b/c'
      ex.in.body = '/z'
      processor.process(ex)
      assert ex.in.body == 'http://www.google.com/z'

      ex.in.headers['contextURI'] = 'http://www.google.com/a/b/c'
      ex.in.body = 'z'
      processor.process(ex)
      assert ex.in.body == 'http://www.google.com/a/b/z'

      // testing resolution with query string
      ex.in.headers['contextURI'] = 'http://www.google.com/a/b/c'
      ex.in.body = '/z?a=1&b=2'
      processor.process(ex)
      assert ex.in.body == 'http://www.google.com/z?a=1&b=2'

      ex.in.headers['contextURI'] = 'http://www.google.com/a/b/c'
      ex.in.body = 'z?a=1&b=2'
      processor.process(ex)
      assert ex.in.body == 'http://www.google.com/a/b/z?a=1&b=2'

      ex.in.headers['contextURI'] = 'http://www.google.com/a/b/c'
      ex.in.body = '  ?a=1&b=2'
      processor.process(ex)
      assert ex.in.body == 'http://www.google.com/a/b/c?a=1&b=2'

      ex.in.headers['contextURI'] = 'http://www.google.com/a/b/c?pageNum=2'
      ex.in.body = '?a=1&b=2'
      processor.process(ex)
      assert ex.in.body == 'http://www.google.com/a/b/c?a=1&b=2'

      ex.in.headers['contextURI'] = 'http://localhost:8080/a/b/c'
      ex.in.body = 'z?a=1&b=2'
      processor.process(ex)
      assert ex.in.body == 'http://localhost:8080/a/b/z?a=1&b=2'


      // URL Optimization
      ex.in.headers['contextURI'] = 'http://localhost:8080/a/b/c'
      ex.in.body = './map'
      processor.process(ex)
      assert ex.in.body == 'http://localhost:8080/a/b/map'

      // URL Optimization
      ex.in.headers['contextURI'] = 'http://localhost:8080/a/b/c'
      ex.in.body = '#map'
      processor.process(ex)
      assert ex.in.body == 'http://localhost:8080/a/b/c#map'

      // URL Optimization
      ex.in.headers['contextURI'] = 'http://localhost:8080/a/b/c#inode'
      ex.in.body = '#map'
      processor.process(ex)
      assert ex.in.body == 'http://localhost:8080/a/b/c#map'
      
    }
}
