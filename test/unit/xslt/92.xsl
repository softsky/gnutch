<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://shareholders.fortress.com/news.aspx?IID=4147324&amp;mode=1</gn:init>
  <gn:filter>
    +http://shareholders.fortress.com/news.aspx\?IID=\d*&amp;Year=\d*&amp;mode=\d*
    +http://shareholders.fortress.com/news.aspx\?iid=\d*&amp;start=\d*&amp;mode=\d*&amp;Type=AL
    +http://shareholders.fortress.com/file.aspx\?IID=\d*&amp;FID=\d*
  </gn:filter>
  <gn:index>http://shareholders.fortress.com/file.aspx\?IID=\d*&amp;FID=\d*</gn:index>
  <gn:schedule>1 day</gn:schedule>
  <!-- FIXME: lot of INVALID_CHARACTER_ERR: An invalid or illegal XML character is specified -->
  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='PRSection']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="normalize-space(div[@id='PRHedline']/div[@class='hl1'])"/></field>
      <field name="content">
        <xsl:for-each select="div[@id='PRbody']/div[@class='PRBodyTxt']/div[@class='bodyTxt']/p">
          <xsl:value-of select="normalize-space(.)"/>
          <xsl:text>
          </xsl:text>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
