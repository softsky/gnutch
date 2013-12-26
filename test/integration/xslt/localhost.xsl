<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <!-- This file is used for testing. We will use embedded Jetty server to test it -->
  <gn:init>http://localhost:8080/</gn:init>
  <gn:filter>
    +http://localhost:8080/\d\.html
  </gn:filter>
  <gn:index>http://localhost:8080/\d\.html</gn:index>
  <gn:schedule>2 weeks</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
    <doc>
      <field name="id"><xsl:value-of select="$contextURI"/></field>
      <xsl:apply-templates select="/html/body"/>
    </doc>
  </xsl:template>

  <xsl:template match="body">
    <field name="title"><xsl:value-of select="normalize-space(text())"/></field>
    <field name="content"><xsl:value-of select="normalize-space(text())"/></field>
  </xsl:template>

</xsl:stylesheet>
