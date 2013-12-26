<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.ecpinvestments.com/index.php/media/news/</gn:init>
  <gn:filter>
    +http://www.ecpinvestments.com/index.php/\d*/
    +http://www.ecpinvestments.com/index.php/[^\d][^/]*/
    -http://www.ecpinvestments.com/index.php/media.*
    -http://www.ecpinvestments.com/index.php/contact.*
    -http://www.ecpinvestments.com/index.php/portfolio-companies.*
    -http://www.ecpinvestments.com/index.php/about-us-.*
    -http://www.ecpinvestments.com/index.php/responsible-investing.*
    -http://www.ecpinvestments.com/index.php/investor-relations.*
    -http://www.ecpinvestments.com/index.php/our-people.*
    -http://www.ecpinvestments.com/index.php/legal-notice.*
    -http://www.ecpinvestments.com/index.php/privacy-notice.*
    -http://www.ecpinvestments.com/index.php/sitemap.*
  </gn:filter>
  <gn:index>http://www.ecpinvestments.com/index.php/[^\d][^/]*/</gn:index>
  <gn:schedule>1 hour</gn:schedule>

  <xsl:output
      method="xml"
      indent="yes"
      encoding="utf-8"/>

  <xsl:param name="contextURI"/>
  <xsl:template match="/">
      <doc>
        <field name="id"><xsl:value-of select="$contextURI"/></field>
        <xsl:apply-templates select="//div[@id='content']/div[@id='t1MainColumn']"/>
      </doc>
  </xsl:template>

  <xsl:template match="div">
      <field name="title"><xsl:value-of select="string(div[@id='t1MainColumnHeader']/span[@class='pageTitle'])"/></field>
      <field name="content">
        <xsl:for-each select="div[@id='t1MainColumnContent']/p">
          <xsl:value-of select="normalize-space(.)"/>
        </xsl:for-each>
      </field>
  </xsl:template>

</xsl:stylesheet>
