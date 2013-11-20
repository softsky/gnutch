<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:gn="https://github.com/softsky/gnutch"    
    exclude-result-prefixes="gn"
    version="1.0">

  <gn:init>http://www.eurazeo.com/presse/communiques-de-presse-eurazeo</gn:init>
  <gn:filter>
    +http://www.eurazeo.com/presse/communiques-de-presse-entreprises
    +http://www.eurazeo.com/presse/rapports-annuels
    +http://www.eurazeo.com/presse/mediatheque
    +http://www.eurazeo.com/content/download/.+\.pdf
  </gn:filter>
  <gn:index>http://www.eurazeo.com/content/download/.+\.pdf</gn:index>
  <gn:schedule>2 weeks</gn:schedule>

</xsl:stylesheet>
