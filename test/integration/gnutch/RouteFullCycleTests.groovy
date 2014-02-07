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

class RouteFullCycleTests extends CamelTestSupport {

  def grailsApplication

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

    camelContext.shutdownStrategy.timeout = 60 // setting shutdown timeout to 1 minute (60 seconds)

    // Running embedded Jetty server to crawl from. Application sits in test/integation/resources/web-app
    server = new Server(8080);
    ResourceHandler resource_handler = new ResourceHandler();
    resource_handler.directoriesListed = true;
    resource_handler.welcomeFiles = ["index.html"];

    resource_handler.resourceBase = "test/integration/resources/web-app";
          
    HandlerList handlers = new HandlerList();
    handlers.handlers = [resource_handler, new DefaultHandler()];
    server.handler = handlers;
    server.start();
  }

  @After 
  void tearDown(){
    camelContext.stop() // stopping camel ourselves and after stop wiping out activemq queue

    server.stop();

    super.tearDown()
  }

  @Test
  @DirtiesContext
  void testFullCycle() {
    camelContext.
    getRouteDefinition('aggregation').
    adviceWith(camelContext,
               new AdviceWithRouteBuilder() { 
                 @Override
                 public void configure() throws Exception {
                   mockEndpointsAndSkip("direct:publish")
                 }
               });
    def mockEndpoint = getMockEndpoint("mock:direct:publish")

    mockEndpoint.expectedMessageCount(1)
    def expectation = {-> 
      def ex = receivedExchanges[0]
      assert ex.in.body.documentElement.nodeName == 'add'
      assert ex.in.body.getElementsByTagName('doc').length > 0
      
    } as Runnable
    expectation.delegate = mockEndpoint
    mockEndpoint.expects(expectation)

    // saving file
    def resourceStream = new ClassPathResource('xslt/localhost.xsl').inputStream
    def destFile = new File(grailsApplication.config.gnutch.inputRoute.replace('file://', '') + '/localhost.xsl')
    destFile.append(resourceStream)

    assertMockEndpointsSatisfied(15, TimeUnit.SECONDS)
  }
}