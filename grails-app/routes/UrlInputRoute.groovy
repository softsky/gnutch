import org.apache.camel.builder.RouteBuilder

class UrlInputRoute extends RouteBuilder {
  def grailsApplication

  @Override
  void configure() {
      def config = grailsApplication?.config

      from("${config.gnutch.inputRoute}").
      convertBodyTo(org.w3c.dom.Document).
      beanRef('sourceUrlsProducerService', 'produce')
    }
}

