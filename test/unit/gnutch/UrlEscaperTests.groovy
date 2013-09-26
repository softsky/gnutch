package gnutch

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class UrlEscaperTests {

  void testUnescape() {
    assert UrlEscaper.unescape("http://www.abc.com/foo%20bar") == 'http://www.abc.com/foo bar'
    assert UrlEscaper.unescape("http://www.abc.com/foo%foo") == 'http://www.abc.com/foo%foo'
    assert UrlEscaper.unescape("http://www.abc.com/new+global+regulatory%2C") == 'http://www.abc.com/new+global+regulatory,'
  }

}
