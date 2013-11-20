<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.altariscap.com/category/news/</gn:init>
  <gn:filter>
    +http://www.altariscap.com/category/news/page/\d*/
    +http://www.altariscap.com/news/.*
  </gn:filter>
  <gn:index>http://www.altariscap.com/news/.*</gn:index>
  <gn:schedule>3 days</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@class='post-item']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="div[@class='post-title']/h1"/></field>
      <field name="content">
        <xsl:for-each select="p">
          <xsl:value-of select="."/>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
