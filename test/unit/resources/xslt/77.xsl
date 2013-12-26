<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.crystalcapital.com/news-and-events/latest-news/</gn:init>
  <gn:filter>
    +http://www.crystalcapital.com/news-and-events/archive/\d*/.*/
  </gn:filter>
  <gn:index>http://www.crystalcapital.com/news-and-events/archive/\d*/.*/</gn:index>
  <gn:schedule>1 week</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='content']/div[@class='grid9']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="normalize-space(h3)"/></field>
      <field name="content">
        <xsl:for-each select="p|div/p">
          <xsl:value-of select="normalize-space(.)"/>
          <xsl:text>
          </xsl:text>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
