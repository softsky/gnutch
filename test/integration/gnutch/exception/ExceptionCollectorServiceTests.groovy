package gnutch.exception

import static org.junit.Assert.*
import org.junit.*

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange

import org.springframework.test.annotation.DirtiesContext

class ExceptionCollectorServiceTests {

    def exceptionCollectorService

    private CamelContext camelContext
    private Exchange exchange

    @Before
    void setUp() {
        camelContext = new DefaultCamelContext()
        exchange = new DefaultExchange(camelContext)
    }

    @After
    void tearDown() {
        camelContext.stop()
    }

    @Test
    @DirtiesContext
    void testCollectException() {
        exchange.setProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT, new Exception('This is first exception'))
        exchange.in.headers['contextURI'] = 'http://www.example.com/a/1'
        exceptionCollectorService.collectException(exchange)

        exchange.setProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT, new Exception('This is second exception'))
        exchange.in.headers['contextURI'] = 'http://www.example.com/a/2'
        exceptionCollectorService.collectException(exchange)

        exchange.setProperty(org.apache.camel.Exchange.EXCEPTION_CAUGHT, new Exception('This is third exception'))
        exchange.in.headers['contextURI'] = 'http://www.example.com/a/3'
        exceptionCollectorService.collectException(exchange)

        assert exceptionCollectorService.exceptions.size() == 1
        assert exceptionCollectorService.exceptions[Exception].size() == 3
    }
}
