package gnutch.urls

import java.util.Collections
import java.util.regex.Pattern

import org.springframework.core.io.ClassPathResource
import org.apache.commons.logging.LogFactory

class RegexUrlChecker {
  private static def log = LogFactory.getLog(this)

  def allowedPatternList = Collections.synchronizedList([])
  def ignorePatternList = Collections.synchronizedList([])

  /**
   * Using pre-define list of ignore patterns
   * checks the {@param url}. If {@param url} matches any pattern from the list
   * <false> is returned. If does not match any - return <true>
   */
  public boolean check(String url){
    def boolean result = (allowedPatternList.any { pattern -> url.matches(pattern) })  &&
             (ignorePatternList.any { pattern -> url.matches(pattern) }  == false)
    log.trace("Checking ${url}: ${result}")
    return result

  }

  public void loadFromResource(String resourceUrl){
    new ClassPathResource(resourceUrl).inputStream.eachLine { line ->
      line = line.trim() // trimming leading and trialing spaces
      if(line.startsWith('+')){
        allowedPatternList << line.substring(1).trim() // skipping first mark
      } else if(line.startsWith('-')){
        ignorePatternList << line.substring(1).trim() // skipping first mark
      } else if(line.startsWith('#') || line.equals('')){
        // just ignoring
      } else throw new RuntimeException('Invalid regexUrlFilter file format')
    }
  }
}