<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://stackoverflow.com/users/</gn:init>
  <gn:filter>
    <![CDATA[
    +http://stackoverflow.com/users\?(page=\d*&)?tab=reputation&filter=all
    +http://stackoverflow.com/users/\d*/[^/$\?]*
    -http://stackoverflow.com/users/\d*/?$
    -http://stackoverflow.com/.*#.* 
    ]]>
  </gn:filter>
  <gn:index>http://stackoverflow.com/users/\d*/.*</gn:index>
  <gn:schedule>2 weeks</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>

<xsl:template name="string-replace-all">
  <xsl:param name="text" />
  <xsl:param name="replace" />
  <xsl:param name="by" />
  <xsl:choose>
    <xsl:when test="contains($text, $replace)">
      <xsl:value-of select="substring-before($text,$replace)" />
      <xsl:value-of select="$by" />
      <xsl:call-template name="string-replace-all">
        <xsl:with-param name="text"
        select="substring-after($text,$replace)" />
        <xsl:with-param name="replace" select="$replace" />
        <xsl:with-param name="by" select="$by" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$text" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

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
      <field name="seen"><xsl:value-of select="table/tbody[2]/tr[2]/td[@class='cool']/span[@class='relativetime']"/></field>
      <field name="website"><xsl:value-of select="table/tbody[1]/tr[1]/td[2]/a[@class='url']/text()"/></field>
      <field name="location"><xsl:value-of select="string(table/tbody[1]/tr[2]/td[@class='label adr']/text())"/></field>
      <field name="age"><xsl:value-of select="concat('0',string(table/tbody[1]/tr[3]/td[2]/text()))"/></field>
    </xsl:for-each>
    <field name="about-me"><xsl:value-of select="string(div[@id='user-info-container']/div[@id='large-user-info']/div[@class='user-about-me note'])"/></field>

    <xsl:variable name="reputation">
      <xsl:value-of select="string(div[@id='user-info-container']/div[@id='large-user-info']/div[@class='user-header-left']/div[@class='gravatar']/div[@class='reputation']/span/a/text())"/>
    </xsl:variable>
    <field name="reputation">
      <xsl:call-template name="string-replace-all">
        <xsl:with-param name="text" select="$reputation" />
        <xsl:with-param name="replace" select="','" />
        <xsl:with-param name="by" select="''" />
      </xsl:call-template>
    </field>

    <xsl:for-each select="div/div[@id='user-panel-accounts']/div[@class='user-panel-content']/table/tbody/tr">
      <xsl:variable name="reputation0">
        <xsl:value-of select="td[@class='reputation']/span"/>
      </xsl:variable>
      <xsl:variable name="reputation">
        <xsl:call-template name="string-replace-all">
          <xsl:with-param name="text" select="$reputation0" />
          <xsl:with-param name="replace" select="','" />
          <xsl:with-param name="by" select="''" />
        </xsl:call-template>
   
      </xsl:variable>
      <field name="accounts"><xsl:value-of select="normalize-space(td[2]/a/text())"/>,<xsl:value-of select="$reputation"/>,<xsl:value-of select="normalize-space(concat('0', td[@class='badges']))"/></field>
    </xsl:for-each>


    <xsl:for-each select="div[4]/div[@id='user-panel-tags']/div[@class='user-panel-content']/table[@class='user-tags']/tbody/tr/td">
      <field name="tags"><xsl:value-of select="a[@class='post-tag']/text()"/>,<xsl:value-of select="concat('0',string(span/span[@class='item-multiplier-count']/text()))"/></field>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
