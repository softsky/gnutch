<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://blackstreetcapital.com/news/</gn:init>
  <gn:filter>
    +http://blackstreetcapital.com/news/page/\d*/
    +http://blackstreetcapital.com/[^/]*/
    -http://blackstreetcapital.com/.*#.*
    -http://blackstreetcapital.com/team.*
    -http://blackstreetcapital.com/overview.*
    -http://blackstreetcapital.com/portfolio.*
    -http://blackstreetcapital.com/tag.*
    -http://blackstreetcapital.com/contact.*
    -http://blackstreetcapital.com/category.*
    -http://blackstreetcapital.com/author.*
    -http://blackstreetcapital.com/pages.*
  </gn:filter>
  <gn:index>http://blackstreetcapital.com/[^/]*/</gn:index>
  <gn:schedule>1 month</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@class='post-body']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="normalize-space(string(header/h1))"/></field>
      <field name="content">
        <xsl:for-each select="section/*">
          <xsl:value-of select="normalize-space(string(.))"/>
          <xsl:text>
          </xsl:text>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
