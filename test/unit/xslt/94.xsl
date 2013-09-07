<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.generalatlantic.com/en/news/overview</gn:init>
  <gn:filter>
    +http://www.generalatlantic.com/en/news/article/\d*
  </gn:filter>
  <gn:index>http://www.generalatlantic.com/en/news/article/\d*</gn:index>
  <gn:schedule>1 day</gn:schedule>
  <!-- TODO: check the website. Lot of Maximum redirects (100) exceeded errors -->
  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='contentBtm']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="normalize-space(div[@id='colA']/h4)"/></field>
      <field name="content">
        <xsl:for-each select="div[@id='colA']/p">
          <xsl:value-of select="normalize-space()"/>
          <xsl:text>
          </xsl:text>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
