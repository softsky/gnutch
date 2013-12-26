<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://ir.americancapital.com/phoenix.zhtml?c=109982&amp;p=irol-news</gn:init>
  <gn:filter>
    +http://ir.americancapital.com/phoenix.zhtml\?c=109982&amp;p=irol-news\d*
    +http://ir.americancapital.com/phoenix.zhtml\?c=\d*&amp;p=irol-newsArticle&amp;ID=\d*&amp;highlight=
  </gn:filter>
  <gn:index>http://ir.americancapital.com/phoenix.zhtml\?c=\d*&amp;p=irol-newsArticle&amp;ID=\d*&amp;highlight=</gn:index>
  <gn:schedule>1 day</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='cbody']/div/div[@class='c580']/div[@class='ccbnContent']/table[1]"/>
      </doc>
  </xsl:template>

  <xsl:template match="table">
      <field name="title"><xsl:value-of select="string(tr[2])"/></field>
      <field name="content"><xsl:value-of select="string(tr[3])"/></field>
  </xsl:template>

</xsl:stylesheet>
