<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
  

  <xsl:import href="header.xsl" />
  <xsl:import href="record.xsl" />
  <xsl:import href="identify.xsl" />
  <xsl:import href="listRecords.xsl" />
  <xsl:import href="listSets.xsl" />
  <xsl:output method="xml" indent="yes" />

  <xsl:template match="amcr|rdf:RDF">
    <xsl:param name="params" />
    <xsl:param name="foundBefore" />
    <xsl:param name="linkClass" />
    <xsl:param name="offset" />
    <xsl:param name="hasNext" />

    <xsl:variable name="pageSize" select="number($params/str[@name='rows'])" />

    <xsl:variable name="verb" select="string($params/str[@name='verb'])" />

    <xsl:choose>
      <!-- Identify -->
      <xsl:when test="$verb = 'Identify'">
        <xsl:call-template name="identify">
          <xsl:with-param name="params" select="$params" />
        </xsl:call-template>
      </xsl:when>

      <!-- ListMetadataFormats -->
      <xsl:when test="$verb = 'ListMetadataFormats'">
        <xsl:call-template name="listFormatsCond">
          <xsl:with-param name="params" select="$params" />
        </xsl:call-template>
      </xsl:when>

      <!-- ListIdentifiers -->
      <xsl:when test="$verb = 'ListIdentifiers'">
        <xsl:call-template name="buildRecordList">
          <xsl:with-param name="params" select="$params" />
          <xsl:with-param name="foundBefore" select="$foundBefore" />
          <xsl:with-param name="hasNext" select="$hasNext" />
          <xsl:with-param name="linkClass" select="$linkClass" />
          <xsl:with-param name="offset" select="$offset" />
          <xsl:with-param name="includeData" select="false()" />
        </xsl:call-template>
      </xsl:when>

      <!-- ListRecords -->
      <xsl:when test="$verb = 'ListRecords'">
        <xsl:call-template name="buildRecordList">
          <xsl:with-param name="params" select="$params" />
          <xsl:with-param name="foundBefore" select="$foundBefore" />
          <xsl:with-param name="hasNext" select="$hasNext" />
          <xsl:with-param name="linkClass" select="$linkClass" />
          <xsl:with-param name="offset" select="$offset" />
          <xsl:with-param name="includeData" select="true()" />
        </xsl:call-template>
      </xsl:when>

      <!-- GetRecord -->
      <xsl:when test="$verb = 'GetRecord'">
        <xsl:call-template name="GetRecord">
          <xsl:with-param name="params" select="$params" />
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$verb='ListSets'">
        <xsl:call-template name="buildSets">
          <xsl:with-param name="params" select="$params" />
        </xsl:call-template>
      </xsl:when>

      <!-- Invalid Verb Error -->
      <xsl:otherwise>
        <error code="badVerb">
          <xsl:value-of select="$params/str[@name='badVerb']" />
          <xsl:value-of select="$verb" />
        </error>
      </xsl:otherwise>

    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>