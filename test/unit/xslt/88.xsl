<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>https://www.firstreserve.com/go.asp?Go=!SiteStation&amp;x=TPLGen&amp;ResType=Folder&amp;ResID=644&amp;TPL=NewsFolderTemplate.htm</gn:init>
  <gn:filter>
    +https://www.firstreserve.com/go.asp\?Go=\!SiteStation&amp;x=TPLGen&amp;ResType=Page&amp;ResID=\d*&amp;TPL=NewsPageTemplate\.htm
  </gn:filter>
  <gn:index>https://www.firstreserve.com/go.asp\?Go=\!SiteStation&amp;x=TPLGen&amp;ResType=Page&amp;ResID=\d*&amp;TPL=NewsPageTemplate\.htm</gn:index>
  <gn:schedule>1 day</gn:schedule>
  <!-- FIXME: mostly NAMESPACE_ERR: An attempt is made to create or change an object in a way which is incorrect with regard to namespaces. -->
  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//table[@width='708']/tr/td/table[@cellpadding='15']/tr/td/blockquote"/>
      </doc>
  </xsl:template>

  <xsl:template match="blockquote">
      <field name="title"><xsl:value-of select="normalize-space(div/b)"/></field>
      <field name="content">
        <xsl:for-each select="div/p">
          <xsl:value-of select="normalize-space(.)"/>
          <xsl:text>
          </xsl:text>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
