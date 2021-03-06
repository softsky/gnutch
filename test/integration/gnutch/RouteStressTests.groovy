package gnutch

import static org.junit.Assert.*
import org.junit.*

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.AdviceWithRouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
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

class RouteStressTests extends CamelTestSupport {
  def grailsApplication
  def config

  ProducerTemplate producerTemplate;
  CamelContext camelContext

  def jmsConnectionFactory

  private Server jettyServer  // jetty jettyServer

  protected CamelContext createCamelContext() throws Exception {
    return camelContext
  }

  @Before
  void setUp(){
    super.setUp()

    config = grailsApplication?.config

    camelContext.start() // starting camel ourselves

    camelContext.shutdownStrategy.timeout = 60 // setting shutdown timeout to 1 minute (60 seconds)

    // Running embedded Jetty jettyServer to crawl from. Application sits in test/integation/resources/web-app
    jettyServer = new Server(8181);
    ResourceHandler resource_handler = new ResourceHandler();
    resource_handler.directoriesListed = true;
    resource_handler.welcomeFiles = ["index.html"];
    resource_handler.resourceBase = "test/integration/resources/web-app";
    HandlerList handlers = new HandlerList();
    handlers.handlers = [resource_handler, new DefaultHandler()];
    jettyServer.handler = handlers;
    jettyServer.start();
  }

  @After
  void tearDown(){
    camelContext.stop() // stopping camel ourselves and after stop wiping out activemq queue

    jettyServer.stop();

    super.tearDown();
  }

  @Test
  @DirtiesContext
  void testStress() {
    camelContext.
    getRouteDefinition('aggregation').
    adviceWith(camelContext,
               new AdviceWithRouteBuilder() {
                 @Override
                 public void configure() throws Exception {
                   mockEndpointsAndSkip("direct:publish")
                 }
               });
    def xsltDir = new ClassPathResource('xslt').file
    xsltDir.eachFile { file ->
      def destFile = new File(config.gnutch.inputRoute.replace('file://', '') + '/' + file.name)
      destFile.append(file.newInputStream())
    }

    def mockEndpoint = getMockEndpoint("mock:direct:publish")

    mockEndpoint.expectedMinimumMessageCount(1) // expecting some messages
    def expectation = {->
      def ex = receivedExchanges[0]
      assert ex.in.body.documentElement.nodeName == 'add'
      assert ex.in.body.getElementsByTagName('doc').length > 0
      println "Commit happened"
    } as Runnable
    expectation.delegate = mockEndpoint
    mockEndpoint.expects(expectation)

    assertMockEndpointsSatisfied(config.gnutch.aggregationTime + 5, TimeUnit.SECONDS) // let this test work 5 seconds longer when we need
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

    // saving file
    def resourceStream = new ClassPathResource('xslt/localhost.xsl').inputStream
    def destFile = new File(config.gnutch.inputRoute.replace('file://', '') + '/localhost.xsl')
    destFile.append(resourceStream)

    mockEndpoint.expectedMessageCount(1)
    def expectation = {->
      def ex = receivedExchanges[0]
      assert ex.in.body.documentElement.nodeName == 'add'
      assert ex.in.body.getElementsByTagName('doc').length == 5 // exactly 5 files should be crawled

    } as Runnable
    expectation.delegate = mockEndpoint
    mockEndpoint.expects(expectation)

    assertMockEndpointsSatisfied(config.gnutch.aggregationTime + 5, TimeUnit.SECONDS) // let this test work 5 seconds longer when we need
  }
}