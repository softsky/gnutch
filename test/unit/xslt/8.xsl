<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.presseportal.de/pm/</gn:init>
  <gn:filter>
    <![CDATA[
             +http://www.presseportal.de/pressemitteilungen/?mode=ir&start=\d*
             +http://www.presseportal.de/pm/\d*/\d*/.*
    ]]>
  </gn:filter>
  <gn:index>http://www.presseportal.de/pm/\d*/\d*/.*</gn:index>
  <!-- FIXME `org.apache.camel.CamelException: Failed to convert the HTML to tidy Markup` on all items-->
  <gn:schedule>2 weeks</gn:schedule>

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

  <xsl:template match="div[@id='story']">
      <field name="title"><xsl:value-of select="h1"/></field>
      <field name="content"><xsl:value-of select="div[@class='fontsizer']/div[@class='easy_text']"/></field>
  </xsl:template>

</xsl:stylesheet>
