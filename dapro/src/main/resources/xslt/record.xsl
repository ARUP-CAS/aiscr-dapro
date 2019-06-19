<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:exslt="http://exslt.org/common"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    exclude-result-prefixes="exslt rdf">
  <xsl:import href="header.xsl" />
  <xsl:output method="xml" indent="yes" />

  <xsl:include href="metadataFormats.xsl" />
  

  <xsl:template name="GetRecord">
    <xsl:param name="params" />

    <xsl:variable name="identifierHead"
        select="string($params/str[@name='identifierHead'])" />

    <xsl:variable name="identifier"
        select="string($params/str[@name='identifier'])" />

    <xsl:variable name="linkClass"
        select="$params/str[@name='link']/@class" />

    <xsl:variable name="localIdentifier"
        select="substring-after($identifier, $identifierHead)" />
    <xsl:choose>
      <xsl:when test="string-length($localIdentifier) = 0">
        <error code="badArgument">
          <xsl:value-of select="$params/str[@name='badArgument']" />
        </error>
      </xsl:when>

      <xsl:when test="not(./*/*[((local-name() = 'ident_cely') or (local-name() = 'filepath')) and (./text() = $localIdentifier)])">
        <xsl:choose>
          <xsl:when test="not(./*[@rdf:about = $identifier])">
            <error code="idDoesNotExist">
              <xsl:value-of select="$params/str[@name='idDoesNotExist']" />
              <xsl:value-of select="$identifier" />
            </error>
          </xsl:when>
          <xsl:otherwise>
            <xsl:for-each select="./*[@rdf:about = $identifier]">
              <xsl:call-template name="record">
                <xsl:with-param name="params" select="$params" />
                <xsl:with-param name="identifier" select="$identifier" />
                <xsl:with-param name="linkClass" select="$linkClass" />
              </xsl:call-template>
            </xsl:for-each>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>

      <xsl:otherwise>
        <xsl:for-each select="./*/*[((local-name() = 'ident_cely') or (local-name() = 'filepath')) and (./text() = $localIdentifier)]/parent::*">
          <xsl:call-template name="record">
            <xsl:with-param name="params" select="$params" />
            <xsl:with-param name="identifier" select="$identifier" />
            <xsl:with-param name="linkClass" select="$linkClass" />
          </xsl:call-template>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="record">
    <xsl:param name="params" />
    <xsl:param name="identifier" />
    <xsl:param name="linkClass" />

    <xsl:variable name="metadataPrefix"
      select="string($params/str[@name='metadataPrefix'])" />

    <xsl:variable name="docRoot">
      <docRoot type="{$metadataPrefix}">
        <xsl:copy-of select="." />
      </docRoot>
    </xsl:variable>

    <xsl:apply-templates select="exslt:node-set($docRoot)">
      <xsl:with-param name="params" select="$params" />
      <xsl:with-param name="identifier" select="$identifier" />
      <xsl:with-param name="linkClass" select="$linkClass" />
    </xsl:apply-templates>
  </xsl:template>

  <!-- Default error doc when none other match -->
  <xsl:template match="doc">
    <error code="cannotDisseminateFormat">
      <xsl:value-of select="//lst[@name='params']/str[@name='cannotDisseminateFormat2']" />
    </error>
  </xsl:template>

</xsl:stylesheet>