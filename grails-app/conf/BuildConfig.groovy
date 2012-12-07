grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

def camelVersion = '2.9.4' // don't upgrade/downgrade camel. it seems best result is using 2.9.4 version
def activeMQVersion = '5.7.0'
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
      excludes 'ehcache'
      // excludes  'jline'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        //mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
      // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
      compile ("org.apache.camel:camel-core:${camelVersion}") { excludes 'slf4j-api' }
      compile ("org.apache.camel:camel-http:${camelVersion}") { excludes 'commons-codec' }
      compile ("org.apache.camel:camel-mail:${camelVersion}") { excludes 'mail' }
      compile ("org.apache.camel:camel-groovy:${camelVersion}") { excludes 'groovy-all' }
      compile ("org.apache.camel:camel-spring:${camelVersion}") { excludes 'log4j' }
      compile ("org.apache.camel:camel-jms:${camelVersion}") 
      compile ("org.apache.camel:camel-cache:${camelVersion}") { excludes 'xercesImpl', 'xml-apis', 'slf4j-api', 'ehcache'  }
      compile ("org.apache.camel:camel-tagsoup:${camelVersion}")

      runtime("org.apache.activemq:activemq-core:${activeMQVersion}")  {excludes 'commons-logging',  'spring-context', 'slf4j-api' }
      runtime("org.apache.activemq:activemq-camel:${activeMQVersion}")  {
        excludes 'commons-logging', 'camel-jms', 'slf4j-api'
      }
      runtime("org.apache.xbean:xbean-spring:3.8") { excludes 'commons-logging' }
      runtime("hsqldb:hsqldb:1.8.0.7")
    }
}
