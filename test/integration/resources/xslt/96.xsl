<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.gipartners.com/european-real-estate-private-equity/news</gn:init>
  <gn:filter>
    +http://www.gipartners.com/european-real-estate-private-equity/news/.*
  </gn:filter>
  <gn:index>http://www.gipartners.com/european-real-estate-private-equity/news/.*</gn:index>
  <gn:schedule>1 day</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='secleft']/div[@class='text1a']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="normalize-space(span[@class='subheader1'])"/></field>
      <field name="content">
        <xsl:for-each select="p|text()|div">
          <xsl:value-of select="normalize-space(.)"/>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
