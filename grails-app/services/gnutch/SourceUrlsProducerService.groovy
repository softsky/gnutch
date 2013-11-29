package gnutch

import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct
import javax.xml.xpath.*
import javax.xml.namespace.NamespaceContext
import java.util.regex.Pattern


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnutch.indexer.DocumentIndexer
import gnutch.urls.PatternService
import gnutch.quartz.SchedulerService
import gnutch.quartz.TimeoutListener

import org.springframework.context.annotation.DependsOn

@DependsOn("camelContext")
class SourceUrlsProducerService {

  static transactional = true

  Logger LOG = LoggerFactory.getLogger(SourceUrlsProducerService.class);

  @Autowired
  def PatternService patternService

  @Autowired
  def DocumentIndexer documentIndexer

  @Autowired
  def SchedulerService schedulerService

  @PostConstruct
  private def init(){
    schedulerService.addTimeoutListener({ key ->
      LOG.trace("job invoked: " + key)
      sendMessage('activemq:input-url', key)
    } as TimeoutListener)
  }

  def produce(def doc) {
    def xpath = XPathFactory.newInstance().newXPath()

    // defining namespace context
    def nsContext = [
      getNamespaceURI: { prefix -> "https://github.com/softsky/gnutch" },
      getPrefix: { uri -> "gn" },
      getPrefixes: { uri -> ["gn"].iterator() } ,
    ] as NamespaceContext

    xpath.setNamespaceContext(nsContext)
    // extracting values for gn:init, gn:filter and gn:index
    def init = xpath.evaluate('gn:init/text()', doc.documentElement)
    def filter = xpath.evaluate('gn:filter/text()', doc.documentElement)
    def index = xpath.evaluate('gn:index/text()', doc.documentElement)
    def schedule = xpath.evaluate('gn:schedule/text()', doc.documentElement)

    // making sure all set
    assert init, "gn:init should be set" 
    assert filter, "gn:filter should be set"
    assert index, "gn:index should be set"
    // gn:scheudle may not be set

    // adding document into map of index:document
    documentIndexer.transformations.put(index, doc) 
  
    // applying gn:filter
    filter.split('\n').each { line ->
      line = line.trim()
      if(line != "")
        switch(line.charAt(0)){
        case '+': L:{
          patternService.addAllowedPattern(Pattern.compile(line.substring(1)))
          LOG.trace('Setting new allow value for RegexUrlChecker:' + line.substring(1));
        };break;
        case '-': L:{
          patternService.addIgnoredPattern(Pattern.compile(line.substring(1)))
          LOG.trace('Setting new ignore value for RegexUrlChecker:' + line.substring(1));
        };break;
          
        default: throw new RuntimeException("gn:filter entry should start with +/-");break;
        }
    }
  
    // if schedule is defined, scheduling job, it will be immediately called regardless to schedule value
    if(schedule){
      def date = schedulerService.scheduleJob(init, schedule)
      LOG.trace("Job scheduled at:" + date)
    } else {
      // if schedule is not defined
      // sending init url into the queue for crawling
      sendMessage('activemq:input-url', init)
    }

  }
}
