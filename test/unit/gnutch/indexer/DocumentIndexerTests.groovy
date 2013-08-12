package gnutch.indexer


import grails.test.*

class DocumentIndexerTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testIsIndexable() {

      def indexer = new DocumentIndexer()
      indexer.transformations << ['^http://stackoverflow.com/users/22656/.*':'stackoverflow.xsl']
      assert indexer.isIndexable('http://stackoverflow.com/users/22656/john') == true
      assert indexer.isIndexable('http://stackoverflow.com/users/a/b/c/') == false

    }
}
