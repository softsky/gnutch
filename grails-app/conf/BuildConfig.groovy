grails.project.work.dir = "target"

String camelVersion = '2.12.1' // don't upgrade/downgrade camel. it seems best result is using 2.9.4 version
String activeMQVersion = '5.8.0'
String tikaVersion  = "1.4"

grails.project.source.level = 1.7
grails.project.target.level = 1.7

grails.project.fork = [

  // configure settings for the test-app JVM, uses the daemon by default

test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

// configure settings for the run-app JVM

run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],

// configure settings for the run-war JVM

war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],

// configure settings for the Console UI JVM

console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]

]

grails.project.dependency.resolution = {

  inherits('global') {
    excludes "aspectjrt"
  }
  log 'warn'

  repositories {    
    grailsCentral()
    mavenCentral()
  }

  dependencies {
    compile ("org.apache.camel:camel-http:${camelVersion}") { excludes 'commons-codec' }
    compile ("org.apache.camel:camel-jms:${camelVersion}")
    compile ("org.apache.camel:camel-cache:${camelVersion}") { excludes 'xercesImpl', 'xml-apis', 'slf4j-api', 'ehcache'  }
    compile ("org.apache.camel:camel-tagsoup:${camelVersion}")

    test ("org.apache.camel:camel-test:${camelVersion}") { excludes "junit" }

    compile ("org.apache.tika:tika-core:${tikaVersion}")
    compile ("org.apache.tika:tika-parsers:${tikaVersion}") { excludes "tika-core", "commons-codec", "commons-logging", "slf4j-api" }

    compile ("org.quartz-scheduler:quartz:2.2.1") { excludes 'slf4j-api' }
      
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

    test("org.eclipse.jetty:jetty-server:9.1.0.v20131115")

  }

  plugins {
    compile(":routing:1.3.0")    
    runtime ":hibernate:3.6.10.6"

    build(":tomcat:7.0.47", ':release:3.0.1', ':rest-client-builder:2.0.1') {
      export = false
    }
  }
}
