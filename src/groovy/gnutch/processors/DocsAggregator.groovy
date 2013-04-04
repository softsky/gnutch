package gnutch.processors

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.w3c.dom.Document
import org.w3c.dom.Node

import org.apache.xpath.XPathAPI
/**
 * Created by IntelliJ IDEA.
 * User: gutsal.arsen
 * Date: 12 серп 2010
 * Time: 9:14:03
 * To change this template use File | Settings | File Templates.
 */
class DocsAggregator implements Processor {
    private static final log = org.apache.log4j.Logger.getLogger(DocsAggregator.class)

    void process(Exchange exchange) {

        def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        def Document parentDoc = builder.parse(new ByteArrayInputStream("<?xml version='1.0'?><add></add>".toString().bytes));
        def groupedExchanges = exchange.properties.find {it.key == 'CamelGroupedExchange'}

        log.debug "+++++++++++++++++++++ ${groupedExchanges.value.size()}"
        groupedExchanges.value.each { Exchange x ->
                def Document doc = x.'in'.body

                def nodes = XPathAPI.selectNodeList(doc.documentElement, '//doc')
                nodes.each { node ->
                  def cloned = parentDoc.adoptNode(node.cloneNode(true))
                  parentDoc.documentElement.appendChild(cloned)
                }
            }
            exchange.in.body = parentDoc
        }
    }
