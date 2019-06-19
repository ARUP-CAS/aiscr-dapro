<xsl:stylesheet version="1.0"
    xmlns="http://www.openarchives.org/OAI/2.0/"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ex="http://exslt.org/dates-and-times"
    xmlns:crm="http://www.cidoc-crm.org/cidoc-crm#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    exclude-result-prefixes="ex crm rdf rdfs">
  <xsl:output method="xml" indent="yes" />

  <xsl:template name="selectDatestamp">
    <xsl:param name="params" />

    <xsl:variable name="datestamp">
      <xsl:for-each select="datum_zapisu|datum_prihlaseni|datum_zapisu_zahajeni|datum_zapisu_ukonceni|datum_navrhu_archivace|datum_archivace|datum_navrzeni_zruseni|datum_zruseni|datum_autorizace|datum_archivace_zaa|datum_odlozeni_nz|datum_vraceni_zaa|datum_podani_nz|datum_zamitnuti|datum|datum_vlozeni|vytvoreno|datum_vymezeni|datum_potvrzeni|.//crm:E50_Date[contains(' datum_zapisu datum_prihlaseni datum_zapisu_zahajeni datum_zapisu_ukonceni datum_navrhu_archivace datum_archivace datum_navrzeni_zruseni datum_zruseni datum_autorizace datum_archivace_zaa datum_odlozeni_nz datum_vraceni_zaa datum_podani_nz datum_zamitnuti datum datum_vlozeni vytvoreno datum_vymezeni datum_potvrzeni ', concat(' ', substring-after(@rdf:about, '#'), ' '))]/rdfs:label|$params/str[@name='datestamp']">
        <xsl:sort order="descending" />
        <xsl:if test="position()=1">
          <xsl:value-of select="." />
        </xsl:if>
      </xsl:for-each>
    </xsl:variable>

    <xsl:value-of select="ex:date($datestamp)" />
  </xsl:template>

  <xsl:template name="header">
    <xsl:param name="params" />
    <xsl:param name="identifier" />
    <xsl:param name="linkClass" />

    <xsl:variable name="datestamp">
      <xsl:call-template name="selectDatestamp">
        <xsl:with-param name="params" select="$params" />
      </xsl:call-template>
    </xsl:variable>

    <header>
      <identifier>
        <xsl:value-of select="$identifier" />
      </identifier>
      <datestamp>
        <xsl:value-of select="$datestamp" />
      </datestamp>
      <setSpec>
        <xsl:value-of select="$linkClass" />
      </setSpec>
    </header>

  </xsl:template>

</xsl:stylesheet>