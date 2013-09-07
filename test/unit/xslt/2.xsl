<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.jflpartners.com/news/</gn:init>
  <gn:filter>
    +http://www.jflpartners.com/news/news_.*\.html
  </gn:filter>
  <gn:index>http://www.jflpartners.com/news/news_.*_i\.html</gn:index>
  <gn:schedule>2 weeks</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
    <doc>
      <field name="id"><xsl:value-of select="$contextURI"/></field>
      <xsl:apply-templates select="//div[@class='Section1']/table[3]"/>
    </doc>
  </xsl:template>

  <xsl:template match="div[@class='Section1']/table">
    <field name="title"><xsl:value-of select="tr[2]/td[3]//font/strong/text()"/></field>
    <field name="content"><xsl:value-of select="tr[3]/td//span[@class='bodytext']"/></field>
  </xsl:template>

</xsl:stylesheet>
