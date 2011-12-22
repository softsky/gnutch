<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fn="http://www.w3.org/2005/xpath-functions"
    xmlns:java="http://xml.apache.org/xalan/java"
    exclude-result-prefixes="fn java"
    version="1.0">

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="/html/body[@class='user-page']/div[@class='container']/div[@id='content']/div[@id='mainbar-full']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div[@id='mainbar-full']">
      <field name="name"><xsl:value-of select=".//table[@class='user-details']//tr[1]/td[@class='fn nickname']/b/text()"/></field>
      <field name="member-for"><xsl:value-of select=".//table[@class='user-details']//tr[2]/td[2]/span[@class='cool']/text()"/></field>
      <field name="seen"><xsl:value-of select=".//table[@class='user-details']//span[@class='supernova']/span[@class='relativetime']"/></field>
      <field name="website"><xsl:value-of select=".//table[@class='user-details']//div[@class='no-overflow']/a[@class='url']/text()"/></field>
      <field name="location"><xsl:value-of select="java:trim(string(.//table[@class='user-details']//td[@class='label adr']/text()))"/></field>
      <field name="age"><xsl:value-of select="concat('0',java:trim(string(.//table[@class='user-details']//tr[6]/td[2]/text())))"/></field>
      <field name="reputation"><xsl:value-of select="java:trim(string(.//td[@class='summaryinfo']/a/span[@class='summarycount']/text()))"/></field>

      <xsl:for-each select="div[@id='tags-table']/table//tr/td[@class='wide-tag-col']">
        <field name="tags"><xsl:value-of select="a[@class='post-tag']/text()"/>,<xsl:value-of select="java:replaceAll(concat('0',string(span[@class='item-multiplier']/text())), '× ', '')"/></field>
      </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>