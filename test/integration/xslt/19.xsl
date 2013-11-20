<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.sgx.com/wps/wcm/connect/design_new/site/sa/news_releases</gn:init>
  <gn:filter>
    +http://www.sgx.com/wps/wcm/connect/sgx_en/home/higlights/news_releases/.*
    +http://www.sgx.com/wps/wcm/connect/design_new/site/sa/news_releases.*
  </gn:filter>
  <gn:index>http://www.sgx.com/wps/wcm/connect/sgx_en/home/higlights/news_releases/.*</gn:index>
  <gn:schedule>2 weeks</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="/html/body/div[@id='container']/div"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="h2"/></field>
      <field name="content">
        <xsl:value-of select="div[@class='gray']"/>
      </field>
  </xsl:template>

</xsl:stylesheet>
