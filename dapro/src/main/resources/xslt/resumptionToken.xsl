<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" />

  <xsl:template name="buildResumptionToken">
    <xsl:param name="params"/>
    <xsl:param name="classCount"/>
    <xsl:param name="foundBefore"/>
    <xsl:param name="hasNext"/>
    <xsl:param name="linkClass"/>
    <xsl:param name="offset"/>

    <xsl:variable name="tokenDay"
        select="string($params/str[@name='tokenDay'])" />

    <xsl:variable name="expirationDateTime"
        select="string($params/str[@name='expirationDateTime'])" />

    <xsl:variable name="metadataPrefix"
        select="string($params/str[@name='metadataPrefix'])" />

    <xsl:variable name="start">
      <xsl:choose>
        <xsl:when test="$params/str[@name='start']">
          <xsl:value-of select="number($params/str[@name='start'])" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="rows"
        select="number($params/str[@name='rows'])" />

    <xsl:variable name="fromDate"
        select="$params/str[@name='from']" />

    <xsl:variable name="untilDate"
        select="$params/str[@name='until']" />

    <xsl:variable name="setScope">
      <xsl:choose>
        <xsl:when test="$params/str[@name='setScope']">
          <xsl:value-of select="$params/str[@name='setScope']" />
        </xsl:when>
        <xsl:otherwise>f</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="pageTail" select="$rows - $foundBefore" />

    <xsl:choose>
      <xsl:when test="$hasNext and ($pageTail > $classCount)">
        <!-- not the last block, so no Resumption Token -->
      </xsl:when>

      <xsl:otherwise>
        <xsl:choose>
          <xsl:when test="not($hasNext) and ($start = 0) and ($pageTail >= $classCount)">
            <!-- getting the entire list at once -->
          </xsl:when>

          <xsl:when test="not($hasNext) and ($pageTail >= $classCount)">
            <!-- at the end of the list, so add empty resumption token -->
            <resumptionToken></resumptionToken>
          </xsl:when>

          <xsl:otherwise>
            <resumptionToken cursor="{$start}">
              <xsl:if test="$expirationDateTime">
                <xsl:attribute name="expirationDate">
                  <xsl:value-of select="$expirationDateTime" />
                </xsl:attribute>
              </xsl:if>

              <xsl:value-of
                  select="concat($tokenDay, '.', $metadataPrefix, '.', $start + $rows, '.', $linkClass, '.', $offset + $pageTail, '.', $rows, '.', $fromDate, '.', $untilDate, '.', $setScope)" />
            </resumptionToken>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
