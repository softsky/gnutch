Very simple alternative to "Apache Nutch":http://nutch.apache.org/ created in Grails

Crawled data could be stored to files, saved to database or even indexed with Apache Solr.
Use "Apache Camel":http://camel.apache.org/ as integration framework and "Apache ActiveMQ":http://activemq.apache.org/ as source messaging and integration patterns server.

## Installation

Depends on :routing:1.2.4 or higher

        add these two lines into your BuildConfig.groovy

        plugins {

        // ....

        build ":routing:1.2.4" // you may use higher version
        build ":gnutch:0.2.2.69"

        // ....
        }


## Configuration

After plugin installation your application will get grails-app/routes folder created with pre-created camel routes
It contains base business logic. You can modify them as you wish.
Also grails-app/conf/ehcache.xml will be created with specific cache definition.

file: `$GNUTCH_HOME/grails-app/conf/Config.groovy` will be appended with that section:

     gnutch {
       // Define local directory which is used for receiving source-definition files.
       inputRoute = 'file:///tmp/gnutch-input'

       crawl {
         // Define fixed number of threads we use for crawling
         threads = 40
       }

       // post processors
       // define custom post processing closure which is called for HTML pages
       // ex.in.body contains org.w3c.dom.Document object represending XHTML page just crawled
       postProcessorHTML = {ex ->}

       // define custom post processing closure which is called for XML document which was received after XSLT
       // ex.in.body contains org.w3c.dom.Document object represending XML result of XSL transformation
       postProcessorXML = {ex ->}


       http {
         // UserAgent string. Better if contain email address of person who is responsible
         // for crawling. That will allow source owners to contact person directly
         userAgent = 'GNutch crawler (https://github.com/softsky/gnutch/). Contact: yourname@domain.com'

         // Since we use shared threadPool for HTTP connections,
         // we define these two values

         // Maximmum number of connections per host
         defaultMaxConnectionsPerHost = 40
         // Maximmum number of total connections
         maxTotalConnections = 40
       }

       solr {
         // URL to Solr server where we send crawled data for indexing
         serverUrl = 'http://localhost:8983/solr'
       }

       activemq {
         // URL to AMQ message broker.
         // For simple configuration it runs embedded AMQ
         // More on possible connection strings here: http://activemq.apache.org/configuring-transports.html
         // brokerURL = 'tcp://0.0.0.0:61616'
         brokerURL = 'vm://localhost'

         // For more complex cases it could contain external AMQ configuration
         //conf = 'classpath:activemq.xml'
       }
     }
