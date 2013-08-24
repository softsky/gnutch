package gnutch.urls

import grails.test.GrailsUnitTestCase

class RegexUrlCheckerTests extends GrailsUnitTestCase {

  private regexUrlChecker = new RegexUrlChecker()

  void testCheckInclusive() {

    [
      /^http.*google\.com\/a.*/,
      /^http.*google\.com\/b.*/
    ].each { regexUrlChecker.allowedPatternList << it }

    [
      /^.*x$/,
      /^.*y$/,
      /^.*z$/
    ].each { regexUrlChecker.ignoredPatternList << it}

    assertTrue regexUrlChecker.check('http://www.google.com/abc')
    assertTrue regexUrlChecker.check('http://www.google.com/bc')
    assertTrue regexUrlChecker.check('http://www.google.com/a/b/c')

    assertFalse regexUrlChecker.check('http://www.google.com/abx') // ends with x
    assertFalse regexUrlChecker.check('http://www.google.com/bcy') // ends with y
    assertFalse regexUrlChecker.check('http://www.google.com/a/b/z') // ends with z
  }

  void testCheckExclusive() {

    regexUrlChecker.allowedPatternList << /^http.*google\.com\/a.*/
    regexUrlChecker.ignoredPatternList <<  /.*\/b.*/

    assertTrue regexUrlChecker.check('http://www.google.com/abc')
    assertFalse regexUrlChecker.check('http://www.google.com/bc')
    assertFalse regexUrlChecker.check('http://www.google.com/a/b/c') // contains /b pattern

    assertFalse regexUrlChecker.check('http://www.yahoo.com/abx') // ends with x
    assertFalse regexUrlChecker.check('http://www.yandex.com/bcy') // ends with y
  }
}
