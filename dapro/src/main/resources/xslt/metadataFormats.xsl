<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    exclude-result-prefixes="rdf">

  <xsl:output method="xml" indent="yes" />

  <xsl:template name="listFormats">
    <xsl:param name="includeDC" select="true()" />
    <xsl:param name="schemaHead" select="'https://api.aiscr.cz/schema/'" />

    <xsl:if test="$includeDC">
      <metadataFormat>
        <metadataPrefix>oai_dc</metadataPrefix>
        <schema>http://www.openarchives.org/OAI/2.0/oai_dc.xsd</schema>
        <metadataNamespace>http://www.openarchives.org/OAI/2.0/oai_dc/</metadataNamespace>
      </metadataFormat>
    </xsl:if>
    <metadataFormat>
      <metadataPrefix>oai_rdf</metadataPrefix> 
      <schema>
        <xsl:value-of select="concat($schemaHead, 'oai_rdf.xsd')" />
      </schema>
      <metadataNamespace>https://api.aiscr.cz/schema/oai_rdf/</metadataNamespace>
    </metadataFormat>
    <metadataFormat>
      <metadataPrefix>oai_amcr</metadataPrefix>
      <schema>
        <xsl:value-of select="concat($schemaHead, 'oai_amcr.xsd')" />
      </schema>
      <metadataNamespace>https://api.aiscr.cz/schema/oai_amcr/</metadataNamespace>
    </metadataFormat>
  </xsl:template>

  <xsl:template name="listFormatsCond">
    <xsl:param name="params" />

    <xsl:variable name="identifierHead"
        select="string($params/str[@name='identifierHead'])" />

    <xsl:variable name="identifier"
        select="string($params/str[@name='identifier'])" />

    <xsl:variable name="localIdentifier"
        select="substring-after($identifier, $identifierHead)" />

    <xsl:variable name="schemaHead"
        select="$params/str[@name='schemaHead']" />

    <xsl:variable name="found">
      <xsl:choose>
        <xsl:when test="string-length($localIdentifier) > 0">
          <xsl:choose>
            <xsl:when test="./*/ident_cely[./text() = $localIdentifier]">
              <xsl:value-of select="1" />
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="count(./*[@rdf:about = $identifier])" />
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="1" />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="linkClass"
        select="$params/str[@name='link']/@class" />

    <xsl:choose>
      <xsl:when test="$found = 0">
        <error code="idDoesNotExist">
          <xsl:value-of select="$params/str[@name='idDoesNotExist']" />
          <xsl:value-of select="$identifier" />
        </error>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="listFormats">
          <xsl:with-param name="includeDC"
              select="not($identifier) or ($linkClass = 'dokument')" />
          <xsl:with-param name="schemaHead" select="$schemaHead" />
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- Metadata format specific stylesheets -->

  <!-- DC -->
  <xsl:include href="metadata_oai_dc.xsl" />

  <xsl:template match="docRoot[@type='oai_dc']/*">
    <xsl:param name="params"/>
    <xsl:param name="identifier"/>
    <xsl:param name="linkClass" />

    <xsl:call-template name="oai_dc">
      <xsl:with-param name="params" select="$params" />
      <xsl:with-param name="identifier" select="$identifier" />
      <xsl:with-param name="linkClass" select="$linkClass" />
    </xsl:call-template>
  </xsl:template>

  <!-- AMCR CIDOC CRM -->
  <xsl:include href="metadata_oai_rdf.xsl" />

  <xsl:template match="docRoot[@type='oai_rdf']/*">
    <xsl:param name="params"/>
    <xsl:param name="identifier"/>
    <xsl:param name="linkClass" />

    <xsl:call-template name="oai_rdf">
      <xsl:with-param name="params" select="$params" />
      <xsl:with-param name="identifier" select="$identifier" />
      <xsl:with-param name="linkClass" select="$linkClass" />
    </xsl:call-template>
  </xsl:template>
  
    <!-- AMCR XML database export -->
  <xsl:include href="metadata_oai_amcr.xsl" />

  <xsl:template match="docRoot[@type='oai_amcr']/*">
    <xsl:param name="params"/>
    <xsl:param name="identifier"/>
    <xsl:param name="linkClass" />

    <xsl:call-template name="oai_amcr">
      <xsl:with-param name="params" select="$params" />
      <xsl:with-param name="identifier" select="$identifier" />
      <xsl:with-param name="linkClass" select="$linkClass" />
    </xsl:call-template>
  </xsl:template>
</xsl:stylesheet>
