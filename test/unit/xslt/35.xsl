<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.3i.com/news/corporate-portfolio-news</gn:init>
  <gn:filter>
    +http://www.3i.com/news/corporate-news/.*
  </gn:filter>
  <gn:index>http://www.3i.com/news/corporate-news/.*</gn:index>
  <gn:schedule>2 weeks</gn:schedule>

  <!-- FIXME: 403 Forbidden exception when trying to request http://www.3i.com/news/corporate-portfolio-news -->
  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>

  <xsl:template match="/">
    <doc>
      <field name="id"><xsl:value-of select="$contextURI"/></field>
      <xsl:apply-templates select="//span[@id='Purecontent1_NewsArticleContent']"/>
    </doc>
  </xsl:template>

  <xsl:template match="span">
    <field name="title"><xsl:value-of select="table[@class='press']/tr[1]/td[2]/text()"/></field>
    <field name="content">
      <xsl:for-each select="p|blockquote/p">
        <xsl:value-of select="."/>
      </xsl:for-each>
    </field>
  </xsl:template>

</xsl:stylesheet>
