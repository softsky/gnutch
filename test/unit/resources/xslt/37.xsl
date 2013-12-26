<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.abraaj.com/news-and-insight/news</gn:init>
  <gn:filter>
    +http://www.abraaj.com/news-and-insight/news/.*
  </gn:filter>
  <gn:index>http://www.abraaj.com/news-and-insight/news/(?!P\d*).*</gn:index>
  <gn:schedule>4 hours</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@class='post-inner']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="h2"/></field>
      <field name="content">
        <xsl:for-each select="p">
          <xsl:value-of select="."/>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
