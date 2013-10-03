package gnutch

import static org.junit.Assert.*
import org.junit.*

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.processor.interceptor.Tracer
import org.apache.camel.processor.interceptor.DefaultTraceFormatter
import org.apache.camel.LoggingLevel

import org.springframework.test.annotation.DirtiesContext

import org.codehaus.groovy.grails.commons.ApplicationHolder
import java.util.concurrent.TimeUnit

import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class RouteTests extends CamelTestSupport {

  def grailsApplication

  ProducerTemplate producerTemplate;
  CamelContext camelContext

  protected CamelContext createCamelContext() throws Exception {
    return camelContext
  }

  @Test
  @DirtiesContext
  void testAggregate() {
    camelContext.
      getRouteDefinition('aggregation').
      adviceWith(camelContext,
                 new AdviceWithRouteBuilder() { 
                   @Override
                   public void configure() throws Exception {
                     mockEndpointsAndSkip("direct:publish")
                   }
                 })

      String [] docs = ['''<doc>
       <field name="title">foo</field>
       <field name="content">bar</field>
      </doc>''', 
      '''<doc>
       <field name="title">foo1</field>
       <field name="content">bar1</field>
      </doc>''',
      '''<doc>
       <field name="title">foo2</field>
       <field name="content">bar2</field>
      </doc>''']

      assertNotNull(camelContext.hasEndpoint('mock:direct:publish'))
      //def mockEndpoint = camelContext.getEndpoint("mock:direct:publish")
      def mockEndpoint = getMockEndpoint("mock:direct:publish")

      mockEndpoint.expectedMessageCount(1)
      def expectation = { 
        def ex = receivedExchanges[0]
        assert ex.in.body.documentElement.nodeName == 'add'
        assert ex.in.body.getElementsByTagName('doc').length == 3
      }
      expectation.delegate = mockEndpoint
      mockEndpoint.expects(expectation)
      docs.each { doc ->
        producerTemplate.sendBody("direct:aggregate-documents", doc) 
      }

      assertMockEndpointsSatisfied()
  }
}
