<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://news.sky.com/us/</gn:init>
  <gn:filter>
    +http://journalists.sky.com/page/.*
    +http://news.sky.com/story/\d*/.*
    -http://news.sky.com/story/\d*/.*#.*
  </gn:filter>
  <gn:index>http://news.sky.com/story/\d*/.*</gn:index>
  <gn:schedule>10 minutes</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='articleWrap']/div[@id='articleText'][1]"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="p[@class='intro']"/></field>
      <field name="content">
      <xsl:for-each select="p[not(@class)]">
        <xsl:value-of select="."/>
      </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
