grails.project.work.dir = "target"

String camelVersion = '2.12.1' // don't upgrade/downgrade camel. it seems best result is using 2.9.4 version
String activeMQVersion = '5.8.0'
String tikaVersion  = "1.4"

grails.project.dependency.resolution = {

  inherits('global') {
    excludes "aspectjrt"
  }
  log 'warn'

  repositories {
    grailsCentral()
    mavenLocal()
    mavenCentral()
  }

  dependencies {
    compile ("org.apache.camel:camel-http:${camelVersion}") { excludes 'commons-codec' }
    compile ("org.apache.camel:camel-jms:${camelVersion}") { excludes 'spring-tx', 'spring-jms','spring-context', 'spring-beans', 'spring-aop', 'spring-core' }
    compile ("org.apache.camel:camel-cache:${camelVersion}") { excludes 'xercesImpl', 'xml-apis', 'slf4j-api', 'ehcache'  }
    compile ("org.apache.camel:camel-tagsoup:${camelVersion}")

    test ("org.apache.camel:camel-test:${camelVersion}") { excludes "junit" }

    compile ("org.quartz-scheduler:quartz:2.1.6") { excludes 'slf4j-api' }

    compile ("org.apache.tika:tika-core:${tikaVersion}")
    compile ("org.apache.tika:tika-parsers:${tikaVersion}") { excludes "tika-core", "commons-codec", "commons-logging", "slf4j-api" }
      
    runtime("org.apache.activemq:activemq-broker:${activeMQVersion}")  {
      excludes 'commons-logging',  'spring-context', 'slf4j-api'
    }

    runtime("org.apache.activemq:activemq-kahadb-store:${activeMQVersion}")  {
      excludes 'spring-context', 'spring-aop', 'spring-core'
    }

    runtime("org.apache.activemq:activemq-camel:${activeMQVersion}")  {
      excludes 'commons-logging', 'slf4j-api', 'camel-core', 'camel-jms', 'camel-spring', 'camel-groovy', 'spring-beans', 'spring-core'
    }
    runtime("org.apache.xbean:xbean-spring:3.8") { excludes 'commons-logging' }

  }

  plugins {
    compile ":routing:1.2.8"
    build ':release:2.2.1', ':rest-client-builder:1.0.3', {
      export = false
    }
  }
}
