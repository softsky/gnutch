package gnutch.urls

import grails.test.*

class RegexUrlCheckerTests extends GrailsUnitTestCase {
  def regexUrlChecker
  protected void setUp() {
    super.setUp()
    regexUrlChecker = new RegexUrlChecker()
  }

  protected void tearDown() {
    super.tearDown()
  }

  void testCheckInclusive() {
        
    [
      /^http.*google\.com\/a.*/,
      /^http.*google\.com\/b.*/
    ].each { regexUrlChecker.allowedPatternList << it }

    [
      /^.*x$/,
      /^.*y$/,
      /^.*z$/
    ].each { regexUrlChecker.ignorePatternList << it}

    assert regexUrlChecker.check('http://www.google.com/abc') == true
    assert regexUrlChecker.check('http://www.google.com/bc') == true
    assert regexUrlChecker.check('http://www.google.com/a/b/c') == true

    assert regexUrlChecker.check('http://www.google.com/abx') == false // ends with x
    assert regexUrlChecker.check('http://www.google.com/bcy') == false // ends with y
    assert regexUrlChecker.check('http://www.google.com/a/b/z') == false // ends with z

  }
  void testCheckExclusive() {

    regexUrlChecker.allowedPatternList << /^http.*google\.com\/a.*/
    regexUrlChecker.ignorePatternList <<  /.*\/b.*/

    assert regexUrlChecker.check('http://www.google.com/abc') == true
    assert regexUrlChecker.check('http://www.google.com/bc') == false
    assert regexUrlChecker.check('http://www.google.com/a/b/c') == false // contains /b pattern

    assert regexUrlChecker.check('http://www.yahoo.com/abx') == false // ends with x
    assert regexUrlChecker.check('http://www.yandex.com/bcy') == false // ends with y

  }
}
