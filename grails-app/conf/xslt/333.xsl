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
        <xsl:apply-templates select="//div[@id='content']//div[@class='specialcontent']/table"/>
      </doc>
  </xsl:template>

  <xsl:template match="table">
  		<xsl:variable name="header-row" select="./tr[td/strong]" />
  		<xsl:variable name="title" select="$header-row/td/strong" />
      <field name="title"><xsl:value-of select="normalize-space($title)"/></field>
      <field name="content">
      	<xsl:for-each select="$title/following-sibling::*">
        	<xsl:value-of select="concat(normalize-space(),' ')"/>
      	</xsl:for-each>
      	<xsl:value-of select="normalize-space($header-row/following-sibling::tr[1])"/>
      </field>
  </xsl:template>
  
  <xsl:template match="text()" />

</xsl:stylesheet>