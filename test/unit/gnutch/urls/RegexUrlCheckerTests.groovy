package gnutch.urls

import grails.test.*

class RegexUrlCheckerTests extends GrailsUnitTestCase {
  def regexUrlChecker
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testCheck() {
      regexUrlChecker = new RegexUrlChecker(
        allowedPatternList: [
          /^http.*google\.com\/a.*/,
          /^http.*google\.com\/b.*/
        ],
        ignorePatternList: [
          /^.*x$/,
          /^.*y$/,
          /^.*z$/
        ])
      assert regexUrlChecker.check('http://www.google.com/abc') == true
      assert regexUrlChecker.check('http://www.google.com/bc') == true
      assert regexUrlChecker.check('http://www.google.com/a/b/c') == true

      assert regexUrlChecker.check('http://www.google.com/abx') == false // ends with x
      assert regexUrlChecker.check('http://www.google.com/bcy') == false // ends with y
      assert regexUrlChecker.check('http://www.google.com/a/b/z') == false // ends with z

      regexUrlChecker = new RegexUrlChecker(
        allowedPatternList: [
          /^http.*google\.com\/a.*/
        ],
        ignorePatternList: [
          /.*/
        ])
      assert regexUrlChecker.check('http://www.google.com/abc') == true
      assert regexUrlChecker.check('http://www.google.com/bc') == false
      assert regexUrlChecker.check('http://www.google.com/a/b/c') == true

      assert regexUrlChecker.check('http://www.yahoo.com/abx') == false // ends with x
      assert regexUrlChecker.check('http://www.yandex.com/bcy') == false // ends with y

    }

  // TODO add loadFromFile method test
}
