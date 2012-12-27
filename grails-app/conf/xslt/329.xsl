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
        <xsl:apply-templates select="//table//tr[2]//td[2]/p[b]"/>
      </doc>
  </xsl:template>

  <xsl:template match="p">
      <field name="title">
      	<xsl:for-each select="./b">
        	<xsl:value-of select="normalize-space()"/>
      	</xsl:for-each>
      </field>
      <field name="content">
      	<xsl:value-of select="normalize-space(./*[local-name() != 'b'])"/>
      	<xsl:for-each select="./following-sibling::*">
        	<xsl:value-of select="normalize-space()"/>
      	</xsl:for-each>
      </field>
  </xsl:template>
  
  <xsl:template match="text()" />

</xsl:stylesheet>