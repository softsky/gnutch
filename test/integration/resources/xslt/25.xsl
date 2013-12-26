<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.businesswire.com/portal/site/home/template.PAGE/news/</gn:init>
  <gn:filter>
    +http://www.businesswire.com/portal/site/home/template.PAGE/news/\?.*
    +http://www.businesswire.com/news/home/\d*/.*
  </gn:filter>
  <gn:index>http://www.businesswire.com/news/home/\d*/.*</gn:index>
  <gn:schedule>1 minute</gn:schedule>

  <!-- FIXME: Failed to convert the HTML to tidy Markup -->
  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='story']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="div[@class='entry-title']/h1"/></field>
      <field name="content"><xsl:value-of select="div[@class='entry-content']"/></field>
  </xsl:template>

</xsl:stylesheet>
