<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.permira.com/news/permira-news</gn:init>
  <gn:filter>
    +http://www.permira.com/news/permira-news(/\d*)?(\?page=\d*)?
    +http://www.permira.com/site/news/\d*.pdf
  </gn:filter>
  <gn:index>http://www.permira.com/site/news/\d*.pdf</gn:index>
  <gn:schedule>1 day</gn:schedule>

</xsl:stylesheet>
