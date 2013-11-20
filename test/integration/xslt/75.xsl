<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.cppib.com/en/public-media/news-releases.html</gn:init>
  <gn:filter>
    +http://www.cppib.com/content/dam/cppib/common/PDF/.+\.pdf
  </gn:filter>
  <gn:index>http://www.cppib.com/content/dam/cppib/common/PDF/.+\.pdf</gn:index>
  <gn:schedule>2 weeks</gn:schedule>


</xsl:stylesheet>
