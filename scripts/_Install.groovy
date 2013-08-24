//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//

println '''
*******************************************************
* You\'ve installed the GNutch grails plugin          *
*                                                     *
* Make sure you have the following plugins installed: *
*    routing (1.2.4+)                                 *
*******************************************************
'''

println "${basedir}/grails-app/routes"
ant.mkdir(dir:"${basedir}/grails-app/routes")

println "Copying pre-defined routes into ${basedir}/grails-app/routes"
ant.copy(toDir:"${basedir}/grails-app/routes"){
  fileset(dir:"${pluginBasedir}/grails-app/routes") {
    include(name:"**/*.groovy")
  }
}

println "Updating configuration"

def srcFile = "${basedir}/grails-app/conf/Config.groovy"
def dstFile = "${basedir}/grails-app/conf/Config.groovy~"

ant.copy(file:srcFile, tofile:dstFile){
  filterchain{
    concatfilter(append: "${pluginBasedir}/grails-app/conf/Config.groovy.txt")
    tokenfilter(delimoutput:"\n")
  }
}
ant.move(file: dstFile, tofile: srcFile)

ant.copy(file:"${pluginBasedir}/grails-app/conf/ehcache.xml", tofile:"${basedir}/grails-app/conf/ehcache.xml")
