Very simple alternative to "Apache Nutch":http://nutch.apache.org/ created in Grails

Could be used independently from Apache Solr, so crawled data could be stored to files, saved to database etc.
Use "Apache Camel":http://camel.apache.org/ as integration framework and "Apache ActiveMQ":http://activemq.apache.org/ as source messaging and integraation patterns server.

## Installation

        cd gnutch
        grails war

Then copy created WAR to $YOUR_WEBSERVER_WEBAPPS directory

## Configuration
file: $GNUTCH_HOME/grails-app/conf/Config.groovy

 gnutch {
   // Input route definition 
   inputRoute = 'file:///tmp/gnutch-input'
   // text file containing regular expressions to include and exclude URL crawling patterns
   regexUrlFilter = 'regex-urlfilter.txt'
  

   crawl {
     // Crawling thread pool
     threads = 40
   }

   transformations = [
     '^http://www.ziprealty.com/property/[\\d\\w\\-\\_]*/\\d*/detail$':'ziprealty.xsl'
   ]

   http {
     // UserAgent string. Better if contain email address of person who is responsible 
     // for crawling. That will allow source owners to contact person directly
     userAgent = 'ziprealty.com crawling bot (ashfaq.rahman@yahoo.com)'
     // Maximmum number of connections per host
     defaultMaxConnectionsPerHost = 40
     // Maximmum number of total connections
     maxTotalConnections = 40
   }

   solr {
     // URL to Solr server (may reside on master server)
     serverUrl = 'http://localhost:8983/solr'
   }
 
  
   activemq {
     // URL to message broker (may reside on master server)
     brokerURL = 'vm://localhost'
     //conf = 'classpath:activemq.xml'
   } 

 }
