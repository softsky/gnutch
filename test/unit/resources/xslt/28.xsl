<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://techcrunch.com</gn:init>
  <gn:filter>
    +http://techcrunch.com/\d*/\d*/\d*/.*
    +http://techcrunch.com/page/\d*/
  </gn:filter>
  <gn:index>http://techcrunch.com/\d*/\d*/\d*/.*</gn:index>
  <gn:schedule>10 minutes</gn:schedule>

  <!-- FIXME: Failed to convert the HTML to tidy Markup -->
  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='module-post-detail']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="normalize-space(h1[@class='headline'])"/></field>
      <field name="content">
        <xsl:value-of select="div[@class='body-copy']"/>
      </field>
  </xsl:template>

</xsl:stylesheet>
