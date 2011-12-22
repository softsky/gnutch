package gnutch.urls

import java.util.regex.Pattern

import org.springframework.core.io.ClassPathResource

class RegexUrlChecker {
  def allowedPatternList
  def ignorePatternList

  public RegexUrlChecker(){
    allowedPatternList = []
    ignorePatternList = []
  }

  public RegexUrlChecker(String resourceUrl){
    allowedPatternList = []
    ignorePatternList = []
    loadFromResource(resourceUrl)
  }
  /**
   * Using pre-define list of ignore patterns
   * checks the {@param url}. If {@param url} matches any pattern from the list
   * <false> is returned. If does not match any - return <true>
   */
  public boolean check(String url){
    assert allowedPatternList
    assert ignorePatternList
    return  (allowedPatternList.any { pattern -> url.matches(pattern) })  ||
             (ignorePatternList.any { pattern -> url.matches(pattern) }  == false)

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