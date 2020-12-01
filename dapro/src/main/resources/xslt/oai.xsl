<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:exslt="http://exslt.org/common"
    exclude-result-prefixes="exslt">
  <xsl:import href="metadataFormats.xsl" />
  <xsl:import href="verbs.xsl" />
  <xsl:output method="xml" indent="yes" />

  

  <xsl:template match="stub">
    <xsl:param name="currentDateTime" />

    <xsl:variable name="params" select="//lst[@name='params']" />

    <xsl:variable name="verb" select="string($params/str[@name='verb'])" />

    <xsl:variable name="requestStr"
      select="string($params/str[@name='requestStr'])" />

    <xsl:variable name="metadataPrefix"
      select="string($params/str[@name='metadataPrefix'])" />

    <xsl:variable name="identifier"
      select="string($params/str[@name='identifier'])" />

    <xsl:variable name="from" select="string($params/str[@name='from'])" />

    <xsl:variable name="until" select="string($params/str[@name='until'])" />

    <xsl:variable name="resumptionToken"
      select="string($params/str[@name='resumptionToken'])" />

    <xsl:variable name="set" select="string($params/str[@name='set'])" />

    <xsl:variable name="schemaHead"
        select="$params/str[@name='schemaHead']" />

    <OAI-PMH xmlns="http://www.openarchives.org/OAI/2.0/"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd"
      xmlns:gml="http://www.opengis.net/gml/3.2">

      

      <responseDate>
        <xsl:value-of select="$currentDateTime" />
      </responseDate>

      <!-- Set if the metadataPrefix field is significant and needs to be 
        validated -->
      <!-- True only for GetRecord, ListRecords, ListIdentifiers verbs -->
      <xsl:variable name="validatePrefix"
        select="$verb='GetRecords' or $verb='ListRecords' or $verb='ListIdentifiers'" />

      <xsl:variable name="listFormats">
        <xsl:call-template name="listFormats" >
          <xsl:with-param name="schemaHead" select="$schemaHead" />
        </xsl:call-template>
      </xsl:variable>

      <xsl:variable name="responses">
        <xsl:choose>
          <xsl:when
            test="($validatePrefix = true()) and (not(exslt:node-set($listFormats)//metadataPrefix[text() = $metadataPrefix]))">
            <error code="cannotDisseminateFormat">
              <xsl:value-of select="$params/str[@name='cannotDisseminateFormat']"/>
              <xsl:value-of select="$metadataPrefix" />
            </error>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="processLinks">
              <xsl:with-param name="params" select="$params" />
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="responseNode" select="exslt:node-set($responses)" />

      <xsl:choose>
        <xsl:when test="name($responseNode/*[1]) = 'error'">
        
          <request>
            <xsl:value-of select="$requestStr" />
          </request>
          <xsl:copy-of select="$responseNode/*[1]" />
        
        </xsl:when>

        <xsl:otherwise>
        
          <xsl:element name="request">
            <xsl:attribute name="verb"><xsl:value-of
              select="$verb" /></xsl:attribute>

            <xsl:if test="string-length($metadataPrefix) > 0">
              <xsl:attribute name="metadataPrefix"><xsl:value-of
                select="$metadataPrefix" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="$identifier">
              <xsl:attribute name="identifier"><xsl:value-of
                select="$identifier" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="string-length($from) > 0">
              <xsl:attribute name="from"><xsl:value-of
                select="$from" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="string-length($until) > 0">
              <xsl:attribute name="until"><xsl:value-of
                select="$until" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="string-length($set) > 0">
              <xsl:attribute name="set"><xsl:value-of
                select="$set" /></xsl:attribute>
            </xsl:if>

            <xsl:if test="string-length($resumptionToken) > 0">
              <xsl:attribute name="resumptionToken"><xsl:value-of
                select="$resumptionToken" /></xsl:attribute>
            </xsl:if>
            <xsl:value-of select="$requestStr" />
            <xsl:text></xsl:text>
          </xsl:element>

          <xsl:element name="{$verb}">
            <xsl:copy-of select="$responseNode" />
          </xsl:element>
   
        </xsl:otherwise>
      </xsl:choose>

    </OAI-PMH>
  </xsl:template>

  <xsl:template name="processLinks">
    <xsl:param name="params" />
    <xsl:param name="index" select="1" />
    <xsl:param name="foundBefore" select="0" />

    <xsl:variable name="pageSize" select="number($params/str[@name='rows'])" />

    <xsl:variable name="offset">
      <xsl:choose>
        <xsl:when test="($index = 1) and $params/str[@name='offset']">
          <xsl:value-of select="number($params/str[@name='offset'])" />
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>0</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <!-- verb templates are applied at least once -->
      <xsl:when
          test="(($index = 1) or ($foundBefore &lt; $pageSize)) and $params/str[@name='link'][$index]">
        <xsl:variable name="responses">
          <xsl:apply-templates select="document($params/str[@name='link'][$index]/text())">
            <xsl:with-param name="params" select="$params" />
            <xsl:with-param name="foundBefore" select="$foundBefore" />
            <xsl:with-param name="linkClass"
                select="$params/str[@name='link'][$index]/@class" />
            <xsl:with-param name="offset" select="$offset" />
            <xsl:with-param name="hasNext"
                select="string-length($params/str[@name='link'][$index + 1]/text()) > 0" />
          </xsl:apply-templates>
        </xsl:variable>

        <xsl:copy-of select="$responses" />

        <xsl:variable name="responseNode" select="exslt:node-set($responses)" />

        <xsl:call-template name="processLinks">
          <xsl:with-param name="params" select="$params" />
          <xsl:with-param name="index" select="$index + 1" />
          <xsl:with-param name="foundBefore"
              select="$foundBefore + count($responseNode/*)" />
        </xsl:call-template>
      </xsl:when>

      <xsl:otherwise>
          <xsl:text>
</xsl:text><!-- must produce something to suppress XSLT default-->
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
