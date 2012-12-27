<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns:html="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="fn java html"
    version="1.0">

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//table//table//table/tr/td"/>
      </doc>
  </xsl:template>

  <xsl:template match="td">
      <field name="title"><xsl:value-of select="p[1]/b"/></field>
      <field name="content">
        <xsl:for-each select="p[position() > 1]">
          <xsl:value-of select="string(.)"/>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
