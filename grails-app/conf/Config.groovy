grails.config.locations = [
  "classpath:${appName}-config.properties",
  "classpath:${appName}-config.groovy",
  GnutchConfig,
  "file:${userHome}/.grails/${appName}-config.properties",
  "file:${userHome}/.grails/${appName}-config.groovy" ]

log4j = {
    error 'org.codehaus.groovy.grails',
          'org.springframework',
          'org.hibernate',
          'net.sf.ehcache.hibernate'
}
