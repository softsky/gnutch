<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"

    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://bibleonline.ru/bible</gn:init>
  <gn:filter>
    +http://bibleonline.ru/bible/\w\w\w/\d*/\d*/?
  </gn:filter>
  <gn:index>http://bibleonline.ru/bible/\w\w\w/\d*/\d*/?</gn:index>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  
  <xsl:template match="/">
    <docs>
      <xsl:apply-templates select="//div[@id='biblecont']"/>
    </docs>
  </xsl:template>

  <xsl:template match="div">
    <xsl:for-each select="ol/li">
      <doc>
        <xsl:variable name="value" select="@value"/>
        <field name="id"><xsl:value-of select="concat(substring($contextURI, 29) , $value)"/></field>
        <field name="text"><xsl:value-of select="string(.)"/></field>
      </doc>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
