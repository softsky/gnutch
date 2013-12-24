package gnutch.urls

import java.util.regex.Pattern;

import grails.test.GrailsUnitTestCase

class RegexUrlCheckerTests extends GrailsUnitTestCase {

  private regexUrlChecker
  private patternService

  public void setUp(){
    regexUrlChecker = new RegexUrlChecker()
    patternService = new PatternService()
    regexUrlChecker.patternService = patternService
  }

  void testCheckInclusive() {

    [
      /http.*google\.com\/a.*/,
      /http.*google\.com\/b.*/
    ].each { patternService.getAllowedPatterns() << Pattern.compile(it) }

    [
      /^.*x$/,
      /^.*y$/,
      /^.*z$/
    ].each { patternService.getIgnoredPatterns() << Pattern.compile(it) }

    assertTrue regexUrlChecker.check('http://www.google.com/abc')
    assertTrue regexUrlChecker.check('http://www.google.com/bc')
    assertTrue regexUrlChecker.check('http://www.google.com/a/b/c')

    assertFalse regexUrlChecker.check('http://www.google.com/abx') // ends with x
    assertFalse regexUrlChecker.check('http://www.google.com/bcy') // ends with y
    assertFalse regexUrlChecker.check('http://www.google.com/a/b/z') // ends with z
  }

  void testCheckExclusive() {

    patternService.getAllowedPatterns() << Pattern.compile(/http.*google\.com\/a.*/)
    patternService.getIgnoredPatterns() << Pattern.compile(/.*\/b.*/)

    assertTrue regexUrlChecker.check('http://www.google.com/abc')
    assertFalse regexUrlChecker.check('http://www.google.com/bc') // starts with /b
    assertFalse regexUrlChecker.check('http://www.google.com/a/b/c') // contains /b pattern

    assertFalse regexUrlChecker.check('http://www.yahoo.com/abc') // yahoo
    assertFalse regexUrlChecker.check('http://www.yandex.com/bcy') // yandex
    assertTrue regexUrlChecker.check('http://www.google.com/acy') // starts with /b
    assertFalse regexUrlChecker.check('http://www.google.com/bcy') // starts with /b
  }
}
