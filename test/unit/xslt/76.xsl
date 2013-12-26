<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>https://www.credit-suisse.com/ch/en/news-and-expertise/news/economy.html</gn:init>
  <gn:filter>
    +https://www\.credit-suisse\.com/ch/en/news-and-expertise/news/economy\.html/page-\d+
    +https://www\.credit-suisse\.com/ch/en/news-and-expertise/news/economy/.*/article/pwp/news-and-expertise/\d{4}/\d{2}/en/.*\.html
  </gn:filter>
  <gn:index>https://www\.credit-suisse\.com/ch/en/news-and-expertise/news/economy/.*/article/pwp/news-and-expertise/\d{4}/\d{2}/en/.*\.html</gn:index>
  <gn:schedule>1 week</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
    <xsl:template match="/">
        <doc>
            <field name="id">
                <xsl:value-of select="$contextURI"/>
            </field>
            <xsl:variable name="container" select="//*[@id='mainContent']"/>
            <xsl:variable name="title" select="$container//*[starts-with(@class,'container-title')]"/>
            <field name="title">
                <xsl:value-of select="normalize-space($title)"/>
            </field>
            <field name="content">
                <xsl:apply-templates select="$container/node()[.!=$title"/>
            </field>
        </doc>
    </xsl:template>

    <xsl:template match="text() | @* | *[not(//a)]">
        <xsl:value-of select="concat(normalize-space(),' ')"/>
    </xsl:template>

    <xsl:template match="a">
        <xsl:value-of select="concat(normalize-space(),' (',@href,') ')"/>
    </xsl:template>

    <xsl:template match="a[normalize-space()='' or starts-with(@href,'#') or starts-with(@href,'javascript:')] | script | SCRIPT"/>

    <xsl:template match="*">
        <xsl:apply-templates select="node()"/>
    </xsl:template>

</xsl:stylesheet>
