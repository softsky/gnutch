<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.newswire.ca/en/today</gn:init>
  <gn:filter>
    +http://www.newswire.ca/en/today
    +http://www.newswire.ca/en/story/.*
    +http://www.newswire.ca/en/organization/.*
    +http://www.newswire.ca/en/filter/.*
    -http://www.newswire.ca/en/search.*
  </gn:filter>
  <gn:index>http://www.newswire.ca/en/story/d\*/.*</gn:index>
  <gn:schedule>2 weeks</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='release_content']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div[@id='release_content']">
      <field name="title"><xsl:value-of select="div[@class='content']/h1/span"/></field>
      <field name="content"><xsl:value-of select="div[@id='ReleaseContent']"/></field>
  </xsl:template>

</xsl:stylesheet>
