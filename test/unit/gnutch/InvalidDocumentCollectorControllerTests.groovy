package gnutch

import grails.test.mixin.*

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(InvalidDocumentCollectorController)
class InvalidDocumentCollectorControllerTests {


  void testIndex() {

    def control = mockFor(InvalidDocumentCollectorService)

    control.demand.getDocuments(1..1) { ->
      [
        "123.xsl": ["@title:http://www.example.com/a/b/c", "@content:http://www.example.com/a/b"],
        "818.xsl": ["@title:http://www.jflpartners.com/a/b/c", "@content:http://www.jflpartners.com/a/b"],
      ] as Map<String, List<String>>
    }

    controller.invalidDocumentCollectorService = control.createMock()

    controller.index()

    assert view == '/invalidDocumentCollector/index'
    assert model.documents.size() == 2

    // making sure functions been called
    control.verify()
  }
}
