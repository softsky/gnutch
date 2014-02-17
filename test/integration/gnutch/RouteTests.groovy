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
import org.apache.xpath.XPathAPI

import org.springframework.test.annotation.DirtiesContext

import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

import org.codehaus.groovy.grails.commons.ApplicationHolder
import java.util.concurrent.TimeUnit

import org.codehaus.groovy.grails.io.support.ClassPathResource

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

class RouteTests extends CamelTestSupport {

  def grailsApplication

  def config

  ProducerTemplate producerTemplate;
  CamelContext camelContext

  def jmsConnectionFactory

  private Server server  // jetty server

  protected CamelContext createCamelContext() throws Exception {
    return camelContext
  }

  @Before
  void setUp(){
    super.setUp()
    camelContext.start() // starting camel ourselves
    config = grailsApplication?.config
  }

  @After 
  void tearDown(){
    camelContext.stop() // stopping camel ourselves and after stop wiping out activemq queue

    super.tearDown()
  }

  @Test
  @DirtiesContext
  void testProcessTika() {
    camelContext.
    getRouteDefinition('indexBinary').
    adviceWith(camelContext,
               new AdviceWithRouteBuilder() { 
                 @Override
                 public void configure() throws Exception {
                   mockEndpointsAndSkip("direct:aggregate-documents")
                 }
               });

    def pdfIs = new ClassPathResource('resources/2012-03-21-sunrise.pdf').inputStream
    grailsApplication.mainContext.getBean('documentIndexer').transformations.put("http://www.abc.com", null)

    assertNotNull(camelContext.hasEndpoint('mock:direct:aggregate-documents'))
    def mockEndpoint = getMockEndpoint("mock:direct:aggregate-documents")

    def expectation = { 
      def ex = receivedExchanges[0]
      assertEquals ex.in.body.documentElement.nodeName, 'doc'

      assertEquals XPathAPI.selectSingleNode(ex.in.body, '//doc/field[@name="id"]/text()').nodeValue, 'http://www.abc.com'
      assert XPathAPI.selectSingleNode(ex.in.body, '//doc/field[@name="title"]').textContent?.length() > 0
      assert XPathAPI.selectSingleNode(ex.in.body, '//doc/field[@name="content"]').textContent?.length() > 0
    }
    expectation.delegate = mockEndpoint

    mockEndpoint.expects(expectation)

    producerTemplate.sendBodyAndHeaders("direct:process-tika", pdfIs, [contextURI: 'http://www.abc.com'])

    assertMockEndpointsSatisfied(config.gnutch.aggregationTime + 5, TimeUnit.SECONDS)
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

    assertMockEndpointsSatisfied(config.gnutch.aggregationTime + 5, TimeUnit.SECONDS)
  }

}
