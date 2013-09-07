<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">
  <!-- FIXME: does not crawl title, content -->
  <gn:init>http://www.kase.kz/en/news</gn:init>
  <gn:filter>
    +http://www.kase.kz/en/news/kase[\/\d\.]*
    +http://www.kase.kz/en/news/show/\d*
  </gn:filter>
  <gn:index>http://www.kase.kz/en/news/show/\d*</gn:index>
  <gn:schedule>2 weeks</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
    <doc>
      <field name="id"><xsl:value-of select="$contextURI"/></field>
      <xsl:apply-templates select="//body/div"/>
    </doc>
  </xsl:template>

  <xsl:template match="div">
    <field name="title"><xsl:value-of select="b"/></field>
    <field name="content"><xsl:value-of select="pre"/></field>
  </xsl:template>

</xsl:stylesheet>
