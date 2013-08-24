package gnutch.indexer

import grails.test.GrailsUnitTestCase

class DocumentIndexerTests extends GrailsUnitTestCase {

   void testIsIndexable() {
      def indexer = new DocumentIndexer()
      indexer.transformations << ['^http://stackoverflow.com/users/22656/.*':'stackoverflow.xsl']
      assertTrue indexer.isIndexable('http://stackoverflow.com/users/22656/john')
      assertFalse indexer.isIndexable('http://stackoverflow.com/users/a/b/c/')
   }
}
