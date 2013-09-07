<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.denhamcapital.com/newsroom/press-releases/</gn:init>
  <gn:filter>
    +http://www.denhamcapital.com/newsroom/press-releases/\?Nid=\d*
  </gn:filter>
  <gn:index>http://www.denhamcapital.com/newsroom/press-releases/\?Nid=\d*</gn:index>
  <gn:schedule>1 week</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//section[@id='page-content']"/>
      </doc>
  </xsl:template>

  <xsl:template match="section">
      <field name="title"><xsl:value-of select="normalize-space(h1)"/></field>
      <field name="content">
        <xsl:for-each select="div[@class='news_summary']/p">
          <xsl:value-of select="normalize-space(.)"/>
          <xsl:text>
          </xsl:text>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
