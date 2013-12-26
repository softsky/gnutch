<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.goldmansachs.com/media-relations/press-releases-and-comments/current/index.html</gn:init>
  <gn:filter>
    +http://www.goldmansachs.com/media-relations/press-releases-and-comments/archive/index\.html
    +http://www.goldmansachs.com/media-relations/press-releases/(current|archived)/\d*/[\d-]*\.html
  </gn:filter>
  <gn:index>http://www.goldmansachs.com/media-relations/press-releases/(current|archived)/\d*/[\d-]*\.html</gn:index>
  <gn:schedule>1 day</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='content']/section[@class='grid2-padded']"/>
      </doc>
  </xsl:template>

  <xsl:template match="section">
      <field name="title"><xsl:value-of select="normalize-space(h1|article/h1)"/></field>
      <field name="content">
        <xsl:for-each select="p|article/p|text()">
          <xsl:value-of select="normalize-space(.)"/>
          <xsl:text>
          </xsl:text>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
