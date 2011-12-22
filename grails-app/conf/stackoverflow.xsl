<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:java="http://xml.apache.org/xalan/java"
    xmlns:html="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="fn java html"
    version="1.0">

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='mainbar-full']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div[@id='mainbar-full']">
      <field name="name"><xsl:value-of select="div[@class='subheader']/h1[@id='user-displayname']/text()"/></field>
      <xsl:for-each select="div[@id='user-info-container']/div[@id='large-user-info']/div[@class='user-header-left']/div[@class='data']">
        <field name="member-for"><xsl:value-of select="table/tbody[2]/tr[1]/td[@class='cool']/text()"/></field>
        <field name="seen"><xsl:value-of select="table/tbody[2]/tr[2]/td[@class='supernova']/span[@class='relativetime']"/></field>
        <field name="website"><xsl:value-of select="table/tbody[1]/tr[1]/td[2]/a[@class='url']/text()"/></field>
        <field name="location"><xsl:value-of select="java:trim(string(table/tbody[1]/tr[2]/td[@class='label adr']/text()))"/></field>
        <field name="age"><xsl:value-of select="concat('0',java:trim(string(table/tbody[1]/tr[3]/td[2]/text())))"/></field>
      </xsl:for-each>
      <field name="reputation"><xsl:value-of select="java:replaceAll(java:trim(string(div[@id='user-info-container']/div[@id='large-user-info']/div[@class='user-header-left']/div[@class='gravatar']/div[@class='reputation']/span/a/text())), ',', '')"/></field>


      <xsl:for-each select="div[4]/div[@id='user-panel-tags']/div[@class='user-panel-content']/table[@class='user-tags']/tbody/tr/td">
        <field name="tags"><xsl:value-of select="a[@class='post-tag']/text()"/>,<xsl:value-of select="java:replaceAll(concat('0',string(span[@class='item-multiplier']/text())), '× ', '')"/></field>
      </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>