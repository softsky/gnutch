import org.apache.camel.builder.RouteBuilder
import org.w3c.dom.Document

class UrlInputRoute extends RouteBuilder {
  def grailsApplication

  @Override
  void configure() {
      def config = grailsApplication?.config

      from("${config.gnutch.inputRoute}").
      convertBodyTo(Document).
      beanRef('sourceUrlsProducerService', 'produce')
    }
}

