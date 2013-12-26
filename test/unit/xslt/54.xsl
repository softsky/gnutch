<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.berkshirepartners.com/news_press.shtml</gn:init>
  <gn:filter>
    +http://www.berkshirepartners.com/news_press_\d*\w*\d*\.shtml
  </gn:filter>
  <gn:index>http://www.berkshirepartners.com/news_press_\d*\w*\d*\.shtml</gn:index>
  <gn:schedule>1 month</gn:schedule>

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

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="normalize-space(string(h2))"/></field>
      <field name="content">
        <xsl:for-each select="p[not(@class='back')]">
          <xsl:value-of select="normalize-space(string(.))"/>
          <xsl:text>
          </xsl:text>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
