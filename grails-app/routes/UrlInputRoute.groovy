import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class UrlInputRoute {
    def configure = {
      from("${CH.config.gnutch.inputRoute}").
      convertBodyTo(org.w3c.dom.Document).
      beanRef('sourceUrlsProducerService', 'produce')
    }
}
