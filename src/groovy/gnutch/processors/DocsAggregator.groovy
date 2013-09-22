package gnutch.processors

import javax.xml.parsers.DocumentBuilderFactory

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.xpath.XPathAPI
import org.slf4j.LoggerFactory
import org.w3c.dom.Document

/**
 * @author gutsal.arsen
 */
class DocsAggregator implements Processor {
  private static final log = LoggerFactory.getLogger(DocsAggregator.class)

    void process(Exchange exchange) {

        def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        def Document parentDoc = builder.parse(new ByteArrayInputStream("<?xml version='1.0'?><add></add>".bytes))
        def groupedExchanges = exchange.properties.find {it.key == 'CamelGroupedExchange'}

        log.debug "+++++++++++++++++++++ ${groupedExchanges.value.size()}"
        groupedExchanges.value.each { Exchange x ->
            Document doc = x.'in'.body

            def nodes = XPathAPI.selectNodeList(doc.documentElement, '//doc')
            nodes.each { node ->
                def cloned = parentDoc.adoptNode(node.cloneNode(true))
                parentDoc.documentElement.appendChild(cloned)
            }
        }
        exchange.in.body = parentDoc
    }
}
