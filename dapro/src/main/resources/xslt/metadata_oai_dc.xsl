<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
  <xsl:import href="header.xsl" />
  <xsl:output method="xml" indent="yes" />

  <xsl:template name="oai_dc">
    <xsl:param name="params" />
    <xsl:param name="identifier" />
    <xsl:param name="linkClass" />

    <record>
      <xsl:call-template name="header">
        <xsl:with-param name="params" select="$params" />
        <xsl:with-param name="identifier" select="$identifier" />
        <xsl:with-param name="linkClass" select="$linkClass" />
      </xsl:call-template>

      <metadata>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
            xmlns:dc="http://purl.org/dc/elements/1.1/"
            xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:if test="ident_cely">
            <xsl:element name="dc:title">
              <xsl:value-of select="ident_cely" />
            </xsl:element>

            <xsl:element name="dc:identifier">
              <xsl:value-of select="ident_cely"/>
            </xsl:element>

            <xsl:element name="dc:source">
              <xsl:value-of select="concat('https://digiarchiv.aiscr.cz/id/',ident_cely)"/>
            </xsl:element>

          </xsl:if>

          <xsl:if test="let">
            <xsl:element name="dc:relation">
              <xsl:value-of select="let" />
            </xsl:element>
          </xsl:if>

          <xsl:if test="typ_dokumentu">
            <xsl:element name="dc:type">
              <xsl:value-of select="typ_dokumentu" />
            </xsl:element>
          </xsl:if>

          <xsl:if test="material_originalu">
            <xsl:element name="dc:format">
              <xsl:value-of select="material_originalu" />
            </xsl:element>
          </xsl:if>

          <xsl:if test="rada">
            <xsl:element name="dc:type">
              <xsl:value-of select="rada" />
            </xsl:element>
          </xsl:if>

          <xsl:if test="typ_dokumentu_posudek">
            <xsl:element name="dc:subject">
              <xsl:value-of select="typ_dokumentu_posudek" />
            </xsl:element>
          </xsl:if>

          <xsl:if test="autor">
            <xsl:element name="dc:creator">
              <xsl:value-of select="autor" />
            </xsl:element>
          </xsl:if>

          <xsl:if test="organizace">
            <xsl:element name="dc:creator">
              <xsl:value-of select="organizace" />
            </xsl:element>
          </xsl:if>

          <xsl:if test="rok_vzniku">
            <xsl:element name="dc:date">
              <xsl:value-of select="rok_vzniku" />
            </xsl:element>
          </xsl:if>

          <xsl:element name="dc:publisher">Archeologický informační systém České republiky</xsl:element>

          <xsl:if test="jazyk_dokumentu">
            <xsl:element name="dc:language">
              <xsl:value-of select="jazyk_dokumentu" />
            </xsl:element>
          </xsl:if>

          <xsl:if test="ulozeni_originalu">
            <xsl:element name="dc:source">
              <xsl:value-of select="ulozeni_originalu" />
            </xsl:element>
          </xsl:if>

          <xsl:if test="oznaceni_originalu">
            <xsl:element name="dc:source">
              <xsl:value-of select="oznaceni_originalu" />
            </xsl:element>
          </xsl:if>

          <xsl:if test="popis">
            <xsl:element name="dc:description">
              <xsl:value-of select="popis" />
            </xsl:element>
          </xsl:if>

          <xsl:if test="poznamka">
            <xsl:element name="dc:description">
              <xsl:value-of select="poznamka" />
            </xsl:element>
          </xsl:if>

          <xsl:for-each select="extra_data">
            <xsl:if test="cislo_objektu">
              <xsl:element name="dc:subject">
                <xsl:value-of select="cislo_objektu" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="format">
              <xsl:element name="dc:format">
                <xsl:value-of select="format" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="datum_vzniku">
              <xsl:element name="dc:date">
                <xsl:value-of select="datum_vzniku" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="udalost_typ">
              <xsl:element name="dc:subject">
                <xsl:value-of select="udalost_typ" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="udalost">
              <xsl:element name="dc:subject">
                <xsl:value-of select="udalost" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="zeme">
              <xsl:element name="dc:coverage">
                <xsl:value-of select="zeme" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="region">
              <xsl:element name="dc:coverage">
                <xsl:value-of select="region" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="rok_od">
              <xsl:element name="dc:coverage">
                <xsl:value-of select="rok_od" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="rok_do">
              <xsl:element name="dc:coverage">
                <xsl:value-of select="rok_do" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="osoby">
              <xsl:element name="dc:coverage">
                <xsl:value-of select="osoby" />
              </xsl:element>
            </xsl:if>
          </xsl:for-each>

          <xsl:for-each select="tvar">
            <xsl:if test="tvar">
              <xsl:element name="dc:subject">
                <xsl:value-of select="tvar" />
              </xsl:element>
            </xsl:if>
          </xsl:for-each>

          <xsl:for-each select="jednotka_dokumentu">
            <xsl:if test="vazba_akce">
              <xsl:element name="dc:relation">
                <xsl:value-of select="vazba_akce" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="vazba_lokalita">
              <xsl:element name="dc:relation">
                <xsl:value-of select="vazba_lokalita" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="vazba_druha_akce">
              <xsl:element name="dc:relation">
                <xsl:value-of select="vazba_druha_akce" />
              </xsl:element>
            </xsl:if>

            <xsl:if test="vazba_druha_lokalita">
              <xsl:element name="dc:relation">
                <xsl:value-of select="vazba_druha_lokalita" />
              </xsl:element>
            </xsl:if>

            <xsl:for-each select="komponenta_dokument">
              <xsl:if test="obdobi">
                <xsl:element name="dc:coverage">
                  <xsl:value-of select="obdobi" />
                </xsl:element>
              </xsl:if>

              <xsl:if test="presna_datace">
                <xsl:element name="dc:coverage">
                  <xsl:value-of select="presna_datace" />
                </xsl:element>
              </xsl:if>

              <xsl:if test="areal">
                <xsl:element name="dc:subject">
                  <xsl:value-of select="areal" />
                </xsl:element>
              </xsl:if>

              <xsl:for-each select="nalez_dokumentu">
                <xsl:if test="kategorie">
                  <xsl:element name="dc:subject">
                    <xsl:value-of select="kategorie" />
                  </xsl:element>
                </xsl:if>

                <xsl:if test="druh_nalezu">
                  <xsl:element name="dc:subject">
                    <xsl:value-of select="druh_nalezu" />
                  </xsl:element>
                </xsl:if>

                <xsl:if test="specifikace">
                  <xsl:element name="dc:subject">
                    <xsl:value-of select="specifikace" />
                  </xsl:element>
                </xsl:if>

                <xsl:if test="specifikace">
                  <xsl:element name="dc:subject">
                    <xsl:value-of select="specifikace" />
                  </xsl:element>
                </xsl:if>
              </xsl:for-each>
            </xsl:for-each>

            <xsl:for-each select="neident_akce">
              <xsl:if test="okres">
                <xsl:element name="dc:coverage">
                  <xsl:value-of select="okres" />
                </xsl:element>
              </xsl:if>

              <xsl:if test="katastr">
                <xsl:element name="dc:coverage">
                  <xsl:value-of select="katastr" />
                </xsl:element>
              </xsl:if>

              <xsl:if test="vedouci">
                <xsl:element name="dc:coverage">
                  <xsl:value-of select="vedouci" />
                </xsl:element>
              </xsl:if>

              <xsl:if test="rok_zahajeni">
                <xsl:element name="dc:coverage">
                  <xsl:value-of select="rok_zahajeni" />
                </xsl:element>
              </xsl:if>

              <xsl:if test="rok_ukonceni">
                <xsl:element name="dc:coverage">
                  <xsl:value-of select="rok_ukonceni" />
                </xsl:element>
              </xsl:if>

              <xsl:if test="lokalizace">
                <xsl:element name="dc:description">
                  <xsl:value-of select="lokalizace" />
                </xsl:element>
              </xsl:if>

              <xsl:if test="popis">
                <xsl:element name="dc:description">
                  <xsl:value-of select="popis" />
                </xsl:element>
              </xsl:if>
            </xsl:for-each>
          </xsl:for-each>

          <xsl:element name="dc:rights">CC-BY-NC 4.0</xsl:element>
        </oai_dc:dc>
      </metadata>
    </record>
  </xsl:template>

</xsl:stylesheet>
