<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:crm="http://www.cidoc-crm.org/cidoc-crm#"
    exclude-result-prefixes="rdf rdfs crm">
  <xsl:import href="resumptionToken.xsl" />
  <xsl:output method="xml" indent="yes" />

  <xsl:include href="record.xsl" />
  

  <xsl:template name="buildRecordList">

    <xsl:param name="params" />
    <xsl:param name="foundBefore" />
    <xsl:param name="hasNext" />
    <xsl:param name="linkClass" />
    <xsl:param name="offset" />
    <xsl:param name="includeData" />

    <xsl:variable name="numDocs" select="count(*)"/>

    <xsl:variable name="pageSize" select="number($params/str[@name='rows'])" />

    <xsl:variable name="pageTail" select="$pageSize - $foundBefore" />

    <xsl:variable name="identifierHead"
        select="string($params/str[@name='identifierHead'])" />

    <xsl:if test="$foundBefore = 0 and $numDocs = 0">
      <error code="noRecordsMatch">
        <xsl:value-of select="$params/str[@name='noRecordsMatch']" />
      </error>
    </xsl:if>

    <xsl:for-each select="./*">
      <xsl:if test="position() &lt;= $pageTail">
        <xsl:variable name="rdfIdentifier" select="@rdf:about" />
        <xsl:variable name="identCely">
          <xsl:choose>
            <xsl:when test="ident_cely">
              <xsl:value-of select="ident_cely"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="filepath" />
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:variable name="itemIdentifier">
          <xsl:choose>
            <xsl:when test="$rdfIdentifier">
              <xsl:value-of select="$rdfIdentifier"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="concat($identifierHead, $identCely)" />
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:if test="$includeData">
          <xsl:call-template name="record">
            <xsl:with-param name="identifier" select="$itemIdentifier" />
            <xsl:with-param name="linkClass" select="$linkClass" />
            <xsl:with-param name="params" select="$params" />
          </xsl:call-template>
        </xsl:if>

        <xsl:if test="not($includeData)">
          <xsl:call-template name="header">
            <xsl:with-param name="identifier" select="$itemIdentifier" />
            <xsl:with-param name="linkClass" select="$linkClass" />
            <xsl:with-param name="params" select="$params" />
          </xsl:call-template>
        </xsl:if>
      </xsl:if>
    </xsl:for-each>

    <xsl:call-template name="buildResumptionToken">
      <xsl:with-param name="params" select="$params" />
      <xsl:with-param name="classCount" select="$numDocs" />
      <xsl:with-param name="foundBefore" select="$foundBefore" />
      <xsl:with-param name="hasNext" select="$hasNext" />
      <xsl:with-param name="linkClass" select="$linkClass" />
      <xsl:with-param name="offset" select="$offset" />
    </xsl:call-template>

  </xsl:template>

</xsl:stylesheet>
