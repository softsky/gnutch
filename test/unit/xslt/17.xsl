<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.pse.com.ph/stockMarket/home.html</gn:init>
  <gn:filter>
    +http://www.pse.com.ph/resource/memos/\d*/.*\.pdf
  </gn:filter>
  <gn:index>http://www.pse.com.ph/resource/memos/\d*/.*\.pdf</gn:index>
  <gn:schedule>1 day</gn:schedule>

</xsl:stylesheet>
