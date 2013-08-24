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

println "${basedir}/grails-app/routes"
ant.mkdir(dir:"${basedir}/grails-app/routes")

println "Copying pre-defined routes into ${basedir}/grails-app/routes"
ant.copy(toDir:"${basedir}/grails-app/routes"){
  fileset(dir:"${pluginBasedir}/grails-app/routes") {
    include(name:"**/*.groovy")
  }
}