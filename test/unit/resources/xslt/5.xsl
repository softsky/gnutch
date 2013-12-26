<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>https://europeanequities.nyx.com/en/listings/company-news</gn:init>
  <gn:filter>
    +https://europeanequities.nyx.com/en/content/.*
    +https://europeanequities.nyx.com/en/listings/company-news\??.*
    -https://europeanequities.nyx.com/en/markets/.*
    -https://europeanequities.nyx.com/en/market-status/.*
    -https://europeanequities.nyx.com/en/connecting/.*
    -https://europeanequities.nyx.com/en/node/.*
    -https://europeanequities.nyx.com/en/trading/.*
    -https://europeanequities.nyx.com/.*#.*
  </gn:filter>
  <gn:index>https://europeanequities.nyx.com/en/content/.*</gn:index>
  <gn:schedule>2 weeks</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='content']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div[@id='content']">
      <field name="title"><xsl:value-of select="h1[@id='page-title']"/></field>
      <field name="content"><xsl:value-of select="div/div/div[@class='content']"/></field>
  </xsl:template>

</xsl:stylesheet>
