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
      <!-- HEADER part -->
      <field name="id"><xsl:value-of select="$contextURI"/></field>
      <field name="title"><xsl:value-of select="/html/head/meta[@property='og:title']/@content"/></field>
      <field name="type"><xsl:value-of select="/html/head/meta[@property='og:type']/@content"/></field>
      <field name="lat-lon"><xsl:value-of select="/html/head/meta[@property='og:latitude']/@content"/>,<xsl:value-of select="/html/head/meta[@property='og:longitude']/@content"/></field>
      <field name="street-address"><xsl:value-of select="/html/head/meta[@property='og:street-address']/@content"/></field>
      <field name="locality"><xsl:value-of select="/html/head/meta[@property='og:locality']/@content"/></field>
      <field name="region"><xsl:value-of select="/html/head/meta[@property='og:region']/@content"/></field>
      <field name="postal-code"><xsl:value-of select="/html/head/meta[@property='og:postal-code']/@content"/></field>
      <field name="country-name"><xsl:value-of select="/html/head/meta[@property='og:country-name']/@content"/></field>
      <field name="image"><xsl:value-of select="/html/head/meta[@property='og:image']/@content"/></field>

      <!--  -->
      <xsl:apply-templates select="//div[@class='mainpgcontent mainsemifluid']"/>
    </doc>
  </xsl:template>
  
  
  <xsl:template match="div[@class='mainpgcontent mainsemifluid']">
    <field name="price-for-sale"><xsl:value-of select="java:replaceAll(string(.//div[@class='smallPad']/div[1]/div[1]/div[2]/span[@class='detailValue']/strong/text()), ',', '')"/></field>      
    <xsl:for-each select=".//div[@id='propDetailCont']/dl/dt">
      		<field name="title-{java:replaceAll(java:toLowerCase(java:replaceAll(java:replaceAll(string(./text()), '^\s*|\s*$|[^\w\d\s]',''), ' ','-')), '#', 'N')}">
		  <xsl:value-of select="normalize-space(string(following-sibling::dd/text()))" />
		</field>
		<xsl:text>
		</xsl:text>
      </xsl:for-each>
      <field name="photo"><xsl:value-of select="//img[@id='photoOverlayImage']/@src" /></field>
      <xsl:for-each select="//table[@class='pricehistory']/tr[child::td]">
        <field name="price-history">
        	(<xsl:value-of select="normalize-space(string(td[1]))"/>),<xsl:value-of select="normalize-space(string(td[2]))"/>,<xsl:value-of select="java:replaceAll(normalize-space(string(td[3])),',', '')"/>,<xsl:value-of select="normalize-space(string(td[4]))"/>
        </field>
      </xsl:for-each>
      <xsl:for-each select="//table[@class='mlsdetails']/tr[count(child::td) > 1]">
      	<xsl:if test="td[@class='featureLabel'][1]/nobr/text()">
      		<field name="{java:replaceAll(java:toLowerCase(java:replaceAll(java:replaceAll(string(td[@class='featureLabel'][1]/nobr/text()), '^\s*|\s*$|[^\w\d\s]',''), ' ','-')), '#', 'N')}">
		  <xsl:value-of select="normalize-space(string(td[@class='featureData'][1]/text()))" />
		</field>
		<xsl:text>
		</xsl:text>
        </xsl:if>
      	<xsl:if test="td[@class='featureLabel'][2]/nobr/text()">
		<field name="{java:replaceAll(java:toLowerCase(java:replaceAll(java:replaceAll(string(td[@class='featureLabel'][2]/nobr/text()), '^\s*|\s*$|[^\w\d\s]',''), ' ','-')), '#', 'N')}">
		  <xsl:value-of select="normalize-space(string(td[@class='featureData'][2]/text()))" />
		</field>
		<xsl:text>
		</xsl:text>
        </xsl:if>
      </xsl:for-each>
  </xsl:template>    

</xsl:stylesheet>