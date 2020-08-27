<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" />

  <!-- For Additional details on the Identify verb, refer to the OAI specification at: -->
  <!-- http://www.openarchives.org/OAI/openarchivesprotocol.html -->
  
  <xsl:template name="identify">
    <xsl:param name="params" />
    
    <xsl:variable name="requestStr"
      select="string($params/str[@name='requestStr'])" />
    <repositoryName>AIS CR - Archaeological Map of the Czech Republic</repositoryName>
    <baseURL>
      <xsl:value-of select="$requestStr" />
    </baseURL>
    <protocolVersion>2.0</protocolVersion>
    <adminEmail>info@amapa.cz</adminEmail>
    <earliestDatestamp>1990-01-01</earliestDatestamp>
    <deletedRecord>no</deletedRecord>
    <granularity>YYYY-MM-DD</granularity>
    <description>
      <rightsManifest xmlns="http://www.openarchives.org/OAI/2.0/rights/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/rights/ http://www.openarchives.org/OAI/2.0/rightsManifest.xsd" appliesTo="http://www.openarchives.org/OAI/2.0/entity#metadata">
        <rights>
          <rightsReference ref="http://creativecommons.org/licenses/by-nc/4.0/rdf"/>
        </rights>
      </rightsManifest>
    </description>
    <description>
      <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
        <xsl:element name="dc:title">Archeologická mapa České Republiky (AMČR)</xsl:element>
        <xsl:element name="dc:creator">Archeologický ústav AV ČR, Praha, v.v.i.</xsl:element>
        <xsl:element name="dc:creator">Archeologický ústav AV ČR, Brno, v.v.i.</xsl:element>
        <xsl:element name="dc:identifier">https://api.aiscr.cz/</xsl:element>
        <xsl:element name="dc:identifier">version 1.1.0</xsl:element>
        <xsl:element name="dc:source">http://www.archeologickamapa.cz/</xsl:element>
        <xsl:element name="dc:relation">http://www.aiscr.cz/</xsl:element>
        <xsl:element name="dc:language">cs</xsl:element>
        <xsl:element name="dc:language">en</xsl:element>
        <xsl:element name="dc:description">Archeologická mapa České republiky (AMČR) je informační systém pro sběr, správu a prezentaci dat o archeologických výzkumech na území ČR a pro poznání minulosti Čech, Moravy a Slezska. Obsažená data popisují desetitisíce archeologických výzkumů a jejich konkrétních poznatků. Zahrnují informace o vedoucích výzkumu, o tom, kdy výzkum probíhal, o jeho lokalizaci i to, jaké nálezy a z jakého období výzkum zjistil. Zvláštním typem záznamů jsou údaje o archeologických lokalitách, zjištěných povrchovým i dálkovým průzkumem. Většina záznamů je napojena na úložiště digitálních dokumentů a bibliografický katalog. AMČR je určena profesionálním archeologům, odborníkům z blízkých oborů, studentům i zájemcům z širší veřejnosti, jimž nabízí možnost efektivně pracovat s poznatky získanými generacemi archeologů v naší zemi. Je provozována Archeologickými ústavy AV ČR v Praze a Brně. AMČR je od počátku zamýšlena jako páteřní informační systém české archeologie, a proto již v současné době vznikají její účelová rozšíření pro archeologii pražské památkové rezervace, ale i další moduly. AMČR je tak nejdůležitější součástí širší infrastruktury Archeologický informační systém České republiky (AIS CR), která je zapsána na Cestovní mapě velkých infrastruktur pro výzkum, experimentální vývoj a innovace, schválené Vládou ČR.</xsl:element>
        <xsl:element name="dc:description">Pro jednoznačné označení záznamů jsou užívány identifikátory obsažené v polích &quot;ident_cely&quot; společně se základní doménou &quot;https://api.aiscr.cz/id/&quot;. Identifikátor je tak v argumentech vždy předáván v podobě &quot;https://api.aiscr.cz/id/[ident_cely]&quot; (např. &quot;https://api.aiscr.cz/id/C-201912345&quot;).</xsl:element>
        <xsl:element name="dc:description">http://www.archeologickamapa.cz/help/</xsl:element>
        <xsl:element name="dc:description">https://api.aiscr.cz/media/API_UserGuide.pdf</xsl:element>
      </oai_dc:dc>
    </description>
  </xsl:template>

</xsl:stylesheet>
