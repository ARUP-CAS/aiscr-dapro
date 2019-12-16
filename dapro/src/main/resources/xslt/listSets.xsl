<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" />

  <xsl:template name="buildSets">
    <xsl:param name="params" />

    <set>
      <setSpec>projekt</setSpec>
      <setName>Projekty / Projects</setName>
      <setDescription>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:element name="dc:description">Evidenční jednotky terénní činnosti badatelského nebo záchranného rázu evidované již ve fázi přípravy. Pro vymezení projektu je rozhodující podnět k výzkumu a provádějící subjekt (oprávněná organizace), lokalizace a příp. projektová dokumentace. Na projekt zpravidla navazuje jedna či (méně často) více terénních akcí.</xsl:element>
        </oai_dc:dc>
      </setDescription>
    </set>
    <set>
      <setSpec>akce</setSpec>
      <setName>Akce / Fieldwork Events</setName>
      <setDescription>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:element name="dc:description">Jakákoli logická, tj. prostorově a dobou provedení ucelená a konkrétní osobou/organizací provedená část terénní archeologické činnosti, sloužící jako jednotka evidence. V zásadě může jít o terénní činnost libovolného časového a prostorového rozsahu; u velkých terénních výzkumů (např. výzkumů na liniových stavbách) je doporučováno evidovat terénní práce jako několik akcí, a to podle území katastrů. Akce je zpravidla pokračováním archeologického projektu; v takovém případě hovoříme o projektové akci. V rámci projektu může proběhnout i několik akcí, které se liší prostorovým vymezením, ale teoreticky třeba i sezónou výzkumu. Existují i případy akcí, které nesouvisejí s předem evidovaným projektem. Stává se to zejména při retrospektivním zpracování starších archeologických výzkumů, u neplánovaných výzkumů (např. při náhodných nálezech) nebo při aplikaci některých nedestruktivních terénních metod. U těchto typů akcí není jejich předběžná nebo zpětná evidence jako projektů možná nebo účelná. Dané akce proto evidujeme samostatně a hovoříme o samostatných akcích.</xsl:element>
        </oai_dc:dc>
      </setDescription>
    </set>
    <set>
      <setSpec>lokalita</setSpec>
      <setName>Lokality / Sites</setName>
      <setDescription>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:element name="dc:description">Smyslem kategorie je (i) zařadit do evidence prostorové celky známé z různých druhů vizuálního průzkumu, ale dosud řádně nezkoumané a mezi akcemi tudíž nepodchycené; (ii) neztratit informaci o větších celcích, jelikož mezi akcemi se většinou objevují jen jejich dílčí prozkoumané části, a zároveň (iii) systematizovat evidenci vybraných typů archeologických památek v krajině bez ohledu na způsob jejich vymezení (např. hradiště, mohylníky, hrady, zaniklé obce).</xsl:element>
        </oai_dc:dc>
      </setDescription>
    </set>
    <set>
      <setSpec>let</setSpec>
      <setName>Lety / Flights</setName>
      <setDescription>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:element name="dc:description">Shrnuje záznamy o akcích leteckého průzkumu, při kterých byly pořízeny terénní fotografie. Slouží k evidenci základních údajů o letu, jeho průběhu, okolnostech a podmínkách.</xsl:element>
        </oai_dc:dc>
      </setDescription>
    </set>
    <set>
      <setSpec>dok_jednotka</setSpec>
      <setName>Dokumentační jednotky / Descriptive Units</setName>
      <setDescription>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:element name="dc:description">Archeologické terénní akce členíme na prostorové celky, tzv. dokumentační jednotky. Za dokumentační jednotku lze považovat celý prostor akce, jeho část odpovídající určité skupině nálezů, příp. jeho část definovanou průběhem výzkumu (např. konkrétní sondu). Akce mohou mít jednu nebo více dokumentačních jednotek různých typů, lokalita má právě jednu. Na dokumentační jednotky se vážou komponenty. Komponenta akce je prostorovým, chronologickým a funkčním celkem nálezů z jedné dokumentační jednotky akce, komponenta lokality je analogickou částí lokality. Komponenty jsou určeny přiřazením k určitému areálu aktivity (specifické funkční části sídelního areálu, cf. Neustupný 1986) a období (chronologicko-kulturnímu zařazení). Každé komponentě se dále přiřazuje i výčet aktivit, tedy druhů činností, které jsou v rámci komponenty přímo doloženy. Komponenty jsou arbitrárně vymezenými jednotkami; rozhodujícím hlediskem při jejich vymezení je přehlednost a srozumitelnost záznamu o obsahu akce. V AMČR nepočítáme s podrobným a vyčerpávajícím popisem archeologických nálezů patřících jednotlivým komponentám, nálezy chápeme jen jako specifické vlastnosti komponent, které nám pomáhají charakterizovat pramenné východisko, podle něhož byla komponenta rozpoznána. V rámci AMČR tedy nevzniká pro nálezy (na rozdíl od jiných datových tříd) autoritní seznam položek s jednoznačnými identifikátory; nemovité nálezy mohou být dokonce evidovány v rámci více akcí opakovaně (týž příkop odkrytý opakovaně, základy téže kostelní stavby aj.).</xsl:element>
        </oai_dc:dc>
      </setDescription>
    </set>
    <set>
      <setSpec>dokument</setSpec>
      <setName>Dokumenty / Documents</setName>
      <setDescription>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:element name="dc:description">Prvek dokumentace terénního archeologického výzkumu uložený v archivech ARÚP a ARÚB. Obdobné informační zdroje v jiných institucích chápeme jako externí zdroje; na rozdíl od dokumentů totiž nemůžeme zajistit jejich podrobný metadatový popis ani fulltextovou verzi. Obsah dokumentů lze popsat prostřednictvím komponent dokumentu (analogicky ke komponentám akcí) a nálezů dokumentu. Takový popis má účel zejména u fotografií a plánů, jejichž obsah je pochopitelně vždy užší než obsah celé terénní akce. Jednotky dokumentu propojují dokumenty s dalšími datovými třídami. Každá jednotka dokumentu eviduje nejvýše jednu vazbu na akci či lokalitu, jednu na neidentifikovanou akci a libovolný počet napojení na komponenty dokumentu. Dokumenty (zejména hlášení a nálezové zprávy) někdy obsahují informace o terénních výzkumech, které nejsou reportovány v záznamech archeologických akcí, nebo není zatím mezi dokumentem a záznamem o akci nastavena vazba. Pro tyto případy je v AMČR vytvořena datová třída neidentifikované akce ve formě rozšiřujících informací o jednotce dokumentu. V případě leteckých snímků je u dokumentu zpravidla uveden též odkaz na dílčí let.</xsl:element>
        </oai_dc:dc>
      </setDescription>
    </set>
    <set>
      <setSpec>ext_zdroj</setSpec>
      <setName>Externí zdroje / External Sources</setName>
      <setDescription>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:element name="dc:description">Bibliografický segment AMČR slouží k napojení záznamů o akcích na zdrojové informace v publikacích, odborných zprávách a šedé literatuře. Jako externí zdroje jsou chápány publikace, novinové články, nepublikované odborné zprávy a jiné informační zdroje, které nejsou uloženy v archivech ARÚP a ARÚB a nejsou tudíž zahrnuty mezi dokumenty. Pomocí externích odkazů jsou tyto provázány s akcemi a lokalitami.</xsl:element>
        </oai_dc:dc>
      </setDescription>
    </set>
    <set>
      <setSpec>pian</setSpec>
      <setName>PIAN / Spatial Units</setName>
      <setDescription>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:element name="dc:description">Prostorové vymezení archeologických výzkumů je v AMČR zajišťováno povinným vztahem dokumentačních jednotek k některé z jednotek prostorové identifikace. Tyto jednotky nazýváme PIAN (Prostorová identifikace archeologických nálezů). Každá jednotka PIAN může mít vazby k většímu počtu akcí nebo lokalit a v případě akcí to platí i naopak.</xsl:element>
        </oai_dc:dc>
      </setDescription>
    </set>
    <set>
      <setSpec>soubor</setSpec>
      <setName>Soubory / Files</setName>
      <setDescription>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:element name="dc:description">Fyzickým ekvivalentem dokumentu je soubor, příp. několik souborů uložených v repozitářích Digitálního archivu AMČR. Soubor má svůj vlastní metadatový popis týkající jeho formátu, velikosti, data vzniku apod.</xsl:element>
        </oai_dc:dc>
      </setDescription>
    </set>
    <set>
      <setSpec>samostatny_nalez</setSpec>
      <setName>Samostatný nález / TODO</setName>
      <setDescription>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:element name="dc:description">TODO.</xsl:element>
        </oai_dc:dc>
      </setDescription>
    </set>
    <set>
      <setSpec>adb</setSpec>
      <setName>Archeologický dokumentační bod / TODO</setName>
      <setDescription>
        <oai_dc:dc xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
          <xsl:element name="dc:description">TODO.</xsl:element>
        </oai_dc:dc>
      </setDescription>
    </set>
  </xsl:template>
</xsl:stylesheet>
