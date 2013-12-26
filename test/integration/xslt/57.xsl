<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.blue-wolf.com/press-releases/press-release-news.php</gn:init>
  <gn:filter>
    +http://www.blue-wolf.com/press-releases/archive-press-release-news.php
    +http://www.blue-wolf.com/press-releases/\d*/.*\.php
  </gn:filter>
  <gn:index>http://www.blue-wolf.com/press-releases/\d*/.*\.php</gn:index>
  <gn:schedule>1 month</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='right']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="normalize-space(p[1]/text())"/></field>
      <field name="content">
        <xsl:for-each select="p[position() > 1]">
          <xsl:value-of select="string(.)"/>
          <xsl:text>
          </xsl:text>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
