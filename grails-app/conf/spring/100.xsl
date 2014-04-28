<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.greathillpartners.com/news/list/2013</gn:init>
  <gn:filter>
    +http://www.greathillpartners.com/news/list/(?!2013)\d{4}
    +http://www.greathillpartners.com/news/details/\d*/\d*
  </gn:filter>
  <gn:index>http://www.greathillpartners.com/news/details/\d*/\d*</gn:index>
  <gn:schedule>1 day</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
    <doc>
      <field name="id"><xsl:value-of select="$contextURI"/></field>
      <xsl:variable name="title" select="//*[@id='articles'][1]//*[@class='details'][1]/h5[1]" />
      <field name="title">
      	<xsl:apply-templates select="$title/text()" />
      </field>
      <field name="content">
      	<xsl:for-each select="$title/following-sibling::node()">
          <xsl:apply-templates select="." />
      	</xsl:for-each>
      </field>
    </doc>
  </xsl:template>

  <xsl:template match="text() | @* | *[not(//a)]">
    <xsl:value-of select="concat(normalize-space(),' ')"/>
  </xsl:template>

  <xsl:template match="a">
    <xsl:value-of select="concat(normalize-space(),' (',@href,') ')"/>
  </xsl:template>
  
  <xsl:template match="a[normalize-space()=''] | script | SCRIPT" />

  <xsl:template match="*">
    <xsl:apply-templates select="node()" />
  </xsl:template>


</xsl:stylesheet>
