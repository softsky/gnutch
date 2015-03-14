grails.project.work.dir = "target"

String camelVersion = '2.13.0' // don't upgrade/downgrade camel. it seems best result is using 2.9.4 version
String activeMQVersion = '5.9.0'
String tikaVersion = "1.4"

grails.project.source.level = 1.6
grails.project.target.level = 1.6

grails.project.fork = [

        // configure settings for the test-app JVM, uses the daemon by default

        test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon: true],

// configure settings for the run-app JVM

        run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],

// configure settings for the run-war JVM

        war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],

// configure settings for the Console UI JVM

        console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]

]

grails.project.dependency.resolver = "maven" // or ivy

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
        compile "org.apache.camel:camel-http4:${camelVersion}"
        compile("org.apache.camel:camel-jms:${camelVersion}")
        compile("org.apache.camel:camel-cache:${camelVersion}")
        compile("org.apache.camel:camel-tagsoup:${camelVersion}")

        build("xml-apis:xml-apis:2.0.2")
        compile("org.apache.httpcomponents:httpclient:4.3")

        test("org.apache.camel:camel-test:${camelVersion}")

        compile("org.apache.tika:tika-core:${tikaVersion}")
        compile("org.apache.tika:tika-parsers:${tikaVersion}")

        compile("org.quartz-scheduler:quartz:2.2.1")

        runtime("org.apache.activemq:activemq-broker:${activeMQVersion}")
        runtime("org.apache.activemq:activemq-kahadb-store:${activeMQVersion}")
        runtime("org.apache.activemq:activemq-camel:${activeMQVersion}")
        runtime("org.apache.xbean:xbean-spring:3.8")
        build("xerces:xercesImpl:2.8.0")

        test("org.eclipse.jetty:jetty-server:9.1.0.v20131115")
    }
    management {
      dependency 'org.springframework:spring-beans:4.0.7.RELEASE'
    }    
    plugins {
      // plugins for the build system only
      build ':tomcat:7.0.52.1'
      // plugins for the compile step
      compile ':scaffolding:2.1.0'
      compile ':cache:1.1.3'
      compile ':asset-pipeline:1.8.3'

      // plugins needed at runtime but not for compilation
      runtime ':hibernate4:4.3.5.2' // or ':hibernate:3.6.10.14'
      runtime ':database-migration:1.4.0'
      runtime ':jquery:1.11.0.2'
      compile(":routing:1.4.0")

      build(':release:3.0.1', ':rest-client-builder:2.0.1') {
      }
    }
}
