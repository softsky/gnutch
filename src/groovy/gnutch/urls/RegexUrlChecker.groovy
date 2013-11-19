package gnutch.urls

import org.apache.commons.logging.LogFactory

class RegexUrlChecker {
  private static log = LogFactory.getLog(this)

  def allowedPatternList = Collections.synchronizedList([])
  def ignoredPatternList = Collections.synchronizedList([])

  /**
   * Using pre-define list of ignore patterns
   * checks the {@param url}. If {@param url} matches any pattern from the list
   * <false> is returned. If does not match any - return <true>
   */
  boolean check(String url){
    boolean result, allowedResult, ignoredResult
    synchronized(allowedPatternList){
      allowedResult = (allowedPatternList.any { pattern -> url.matches(pattern) })
    }
    synchronized(ignoredPatternList){
      ignoredResult = ignoredPatternList.any { pattern -> url.matches(pattern) } 
    }

    result = (allowedResult && (ignoredResult == false))
    log.trace("Checking ${url}: ${result}")
    return result
  }
}
