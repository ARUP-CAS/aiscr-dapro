<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="header.xsl" />
  <xsl:output method="xml" indent="yes" />

  <xsl:template name="oai_rdf">
    <xsl:param name="params" />
    <xsl:param name="identifier" />
    <xsl:param name="linkClass" />

    <xsl:variable name="schemaHead">
      <xsl:choose>
        <xsl:when test="$params/str[@name='schemaHead']">
          <xsl:value-of select="$params/str[@name='schemaHead']" />
        </xsl:when>
        <xsl:otherwise>https://api.aiscr.cz/schema/</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <record>
      <xsl:call-template name="header">
        <xsl:with-param name="params" select="$params" />
        <xsl:with-param name="identifier" select="$identifier" />
        <xsl:with-param name="linkClass" select="$linkClass" />
      </xsl:call-template>

      <metadata>
        <oai_rdf:crm2rdf
            xmlns:oai_rdf="https://api.aiscr.cz/schema/oai_rdf"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
          <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">
            <xsl:value-of select="concat('https://api.aiscr.cz/schema/oai_rdf', ' ', $schemaHead, 'oai_rdf.xsd')" />
          </xsl:attribute>
          <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
              xmlns:crm="http://www.cidoc-crm.org/cidoc-crm/">
            <xsl:copy-of select="."/>
          </rdf:RDF>
        </oai_rdf:crm2rdf>
      </metadata>
    </record>
  </xsl:template>

</xsl:stylesheet>
