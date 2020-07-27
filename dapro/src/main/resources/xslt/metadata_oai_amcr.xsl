<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
    xmlns="http://www.openarchives.org/OAI/2.0/"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes" />

  <xsl:import href="header.xsl" />

  <xsl:template name="oai_amcr">
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
        <oai_amcr:amcr
            xmlns:oai_amcr="https://api.aiscr.cz/schema/oai_amcr"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
          <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">
            <xsl:value-of select="concat('https://api.aiscr.cz/schema/oai_amcr', ' ', $schemaHead, 'oai_amcr.xsd')" />
          </xsl:attribute>
          <!-- some processors just copy the literal xmlns declaration above - but not all... -->
          <!-- xsl:copy-of select="document('empty.xml')/*/namespace::*" /-->
          <xsl:copy-of select="."/>
        </oai_amcr:amcr>
      </metadata>
    </record>
  </xsl:template>

</xsl:stylesheet>
