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
        <xsl:apply-templates select="//div[@id='content1']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div[@id='content1']">
      <field name="title"><xsl:value-of select="normalize-space(h2)"/></field>
      <!-- We need to exclude the 'script' tags from the output, so we scan recursively -->
      <field name="content">
        <xsl:apply-templates select="div[@class='article-content']"/>
      </field>
  </xsl:template>
  
  <xsl:template match="node()[not(.//script) and (local-name() != 'script')]">
  	<xsl:value-of select="concat(normalize-space(),' ')"/>
  </xsl:template>
  
  <xsl:template match="node()">
  	<xsl:apply-templates />
  </xsl:template>
  
  <xsl:template match="text()" />

</xsl:stylesheet>