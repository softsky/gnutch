<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.nasdaqomxnordic.com/news/companynews/</gn:init>
  <gn:filter>
    +https://newsclient.omxgroup.com/cdsPublic/viewDisclosure\.action.*
  </gn:filter>
  <gn:index>https://newsclient.omxgroup.com/cdsPublic/viewDisclosure\.action.*</gn:index>
  <gn:schedule>2 weeks</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//table[@id='previewTable']"/>
      </doc>
  </xsl:template>

  <xsl:template match="table">
      <field name="title"><xsl:value-of select="tr[2]/td/h3"/></field>
      <field name="content"><xsl:value-of select="tr[2]/td/p"/></field>
  </xsl:template>

</xsl:stylesheet>
