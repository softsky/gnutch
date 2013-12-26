<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.energyventures.no/ev/NEWS</gn:init>
  <gn:filter>
    +http://www.energyventures.no/ev/content/view/full/\d*/\(year\)/\d*
    +http://www.energyventures.no/NEWS/.*
  </gn:filter>
  <gn:index>http://www.energyventures.no/NEWS/.*</gn:index>
  <gn:schedule>1 day</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@class='content-view-children']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="normalize-space(div[@class='attribute-short']/p/b)"/></field>
      <field name="content">
        <xsl:value-of select="normalize-space(div[@class='attribute-long'])"/>
      </field>
  </xsl:template>

</xsl:stylesheet>
