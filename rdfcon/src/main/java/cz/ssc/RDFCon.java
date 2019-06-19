package cz.ssc;

import cz.ssc.filter.TagNotFoundException;
import cz.ssc.filter.AccessLevel;
import cz.ssc.filter.RDFFilter;
import cz.ssc.filter.FileNames;
import cz.ssc.filter.Constants;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import cz.ssc.filter.Pair;
import eu.delving.x3ml.X3MLEngine;
import eu.delving.x3ml.X3MLGeneratorPolicy;
import eu.delving.x3ml.engine.Generator;
import gr.forth.Labels;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Juraj
 */
public class RDFCon {

    private static final Logger LOGGER = Logger.getLogger(RDFCon.class.getName());
    static final CommandLineParser PARSER = new PosixParser();
    static Options options = new Options();

    public static final String AAO_OPTION_NAME = "allAtOnce";
    public static final String KEYWORD_OPTION_NAME = "keyword";
    public static final int UUID_SIZE = 2;

    /**
     * @param INPUT_PATH
     * @param MAPPINGS_PATH
     * @param GENERATOR_POLICY_PATH
     * @param OUTPUT_FILE_NAME
     * @param KEYWORD
     * @param ALL_AT_ONCE
     * @throws java.io.FileNotFoundException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws cz.ssc.filter.TagNotFoundException
     */
    public static void go(
            String INPUT_PATH,
            String MAPPINGS_PATH,
            String GENERATOR_POLICY_PATH,
            String OUTPUT_FILE_NAME,
            String KEYWORD,
            Boolean ALL_AT_ONCE) throws FileNotFoundException,
            XPathExpressionException, ParserConfigurationException, SAXException,
            IOException, TagNotFoundException {

        X3MLEngine engine = X3MLEngine.load(new FileInputStream(new File(MAPPINGS_PATH)));
        Generator policy = X3MLGeneratorPolicy.load(new FileInputStream(
                new File(GENERATOR_POLICY_PATH)), X3MLGeneratorPolicy.createUUIDSource(UUID_SIZE));

        if (!ALL_AT_ONCE) {
            LOGGER.log(Level.FINE, "Performing conversion per partes ...");

            // Parse the XML into the Document object
            XPathFactory xfactory = XPathFactory.newInstance();
            XPath xpath = xfactory.newXPath();
            XPathExpression allProductsExpression = xpath.compile("/amcr/" + KEYWORD);
            Document doc = getDocument(INPUT_PATH);
            NodeList nodes = (NodeList) allProductsExpression.evaluate(doc, XPathConstants.NODESET);
            LOGGER.log(Level.INFO, "Found {0} results", nodes.getLength());

            Integer convCounter = 0;
            Integer notConvCounter = 0;
            OutputStream outputAll = new FileOutputStream(OUTPUT_FILE_NAME);
            Model finalModel = ModelFactory.createDefaultModel();
            boolean firstNode = true;
            for (int i = 0; i < nodes.getLength(); i++) {
                // Filter the node (if it is not required to be included in the final RDFm throw it away)
                if (!RDFFilter.filter(nodes.item(i), AccessLevel.ANONYM)) {
                    notConvCounter++;
                    continue;
                }

                convCounter++;
                if (convCounter % 100 == 0) {
                    LOGGER.log(Level.INFO, "Converting node {0}+{1}/{2}", new Object[]{convCounter, notConvCounter, nodes.getLength()});
                    printMemData();
                }
                // Creating a valid XML document from the current node and embedding it into the amcr tag
                Node cloneNode = nodes.item(i).cloneNode(true);
                Document xmlDoc = documentBuilderFactory().newDocumentBuilder().newDocument();
                xmlDoc.adoptNode(cloneNode);
                Element root = xmlDoc.createElement("amcr");
                root.appendChild(cloneNode);
                xmlDoc.appendChild(root);

                // Conversion of the document with a single node
                X3MLEngine.Output o = engine.execute(xmlDoc.getDocumentElement(), policy);

                // The way how to add all of the RDF models together (similar as union)
                if (firstNode) {
                    finalModel = o.getModel();
                    firstNode = false;
                } else {
                    finalModel = finalModel.add(o.getModel());
                }
            }

            if (nodes.getLength() != 0) {
                LOGGER.log(Level.INFO, "Filtered out {0}% of nodes",
                        Double.toString(((double) notConvCounter / (double) nodes.getLength()) * 100.0));
            } else {
                LOGGER.log(Level.SEVERE, "ZERO nodes found in input file");
            }

            // Pretty print format not very suitable for large files
            LOGGER.log(Level.FINE, "Writing to file {0}", OUTPUT_FILE_NAME);
            finalModel.write(outputAll, Labels.OUTPUT_FORMAT_RDF_XML_ABBREV);

            // Close the file stream
            outputAll.close();

        } else {
            LOGGER.log(Level.FINE, "Performing conversion all at once ...");
            // Parse XML into Document object
            Element element = getDocument(INPUT_PATH).getDocumentElement();
            // Perform the conversion all at once
            X3MLEngine.Output output = engine.execute(element, policy);
            // Write into the RDF file
            LOGGER.log(Level.FINE, "Writing to file {0}", OUTPUT_FILE_NAME);

            FileOutputStream out = new FileOutputStream(new File(OUTPUT_FILE_NAME));
            output.write(out, Labels.OUTPUT_MIME_TYPE_RDF_XML);
            out.close();
        }

    }

    public static void main(String[] args) {
        try {
            createOptionsList();

            CommandLine cli = PARSER.parse(options, args);

            // Argumenty
            String INPUT_PATH = cli.getOptionValue(Labels.INPUT);
            String MAPPINGS_PATH = cli.getOptionValue(Labels.X3ML);
            String GENERATOR_POLICY_PATH = cli.getOptionValue(Labels.POLICY);
            String OUTPUT_FILE_NAME = cli.getOptionValue(Labels.OUTPUT);
            String KEYWORD = cli.getOptionValue(KEYWORD_OPTION_NAME);
            X3MLEngine.REPORT_PROGRESS = cli.hasOption(Labels.REPORT_PROGRESS);
            Boolean ALL_AT_ONCE = cli.hasOption(AAO_OPTION_NAME);

            Long start, stop;
            // Check if input path and mapping file path is a directory or a file
            Boolean isDir;
            Path inpitDir = new File(INPUT_PATH).toPath();
            Path mapPathDir = new File(MAPPINGS_PATH).toPath();
            Path outDir = new File(OUTPUT_FILE_NAME).toPath();
            if(Files.isDirectory(inpitDir) && Files.isDirectory(mapPathDir) && Files.isDirectory(outDir)){
                isDir = true;
            } else if (Files.isRegularFile(inpitDir) && Files.isRegularFile(mapPathDir)){
                isDir = false;
            } else {
                throw new Exception("Mapping file path, input file and output path must be all files or existing directories.");
            }

            start = System.currentTimeMillis();
            if (isDir) {
                List<Pair<String, String>> files = FileNames.getAllFileInfo(INPUT_PATH, true, true);
                for (Pair p : files) {
                    LOGGER.log(Level.INFO, "----Conversion of file {0}----", p.getSecond());
                    go(
                            (String) p.getSecond(),
                            FileNames.getMappingFileName(MAPPINGS_PATH, (String) p.getFirst()),
                            GENERATOR_POLICY_PATH,
                            FileNames.getDatedOutputName(OUTPUT_FILE_NAME, (String) p.getFirst()),
                            (String) p.getFirst(),
                            ALL_AT_ONCE);
                }
            } else {
                checkParametersOK(MAPPINGS_PATH, KEYWORD);
                go(INPUT_PATH, MAPPINGS_PATH, GENERATOR_POLICY_PATH, OUTPUT_FILE_NAME, KEYWORD, ALL_AT_ONCE);
            }
            stop = System.currentTimeMillis();

            printTime(stop - start);
            printMemData();
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(2);
        }
    }

    public static void checkParametersOK(String mapping_file_path, String keyword) throws Exception {
        Set<String> allowedKW = new HashSet<>();
        allowedKW.add(Constants.EVENT);
        allowedKW.add(Constants.PROJECT);
        allowedKW.add(Constants.DOCUMENT);
        allowedKW.add(Constants.SITE);
        allowedKW.add(Constants.FLIGHT);
        allowedKW.add(Constants.PIAN);
        allowedKW.add(Constants.EXT_SOURCE);
        allowedKW.add(Constants.FILE);
        allowedKW.add(Constants.DOC_UNIT);

        // Keyword is from a set of words
        boolean keyword_from_set = allowedKW.contains(keyword);
        if (!keyword_from_set) {
            throw new Exception("Keyword is invalid.");
        }

        // Mappings file name should contain keyword
        boolean mapping_path = mapping_file_path.contains(keyword);
        if (!mapping_path) {
            throw new Exception("Mapping file path does not contain keyword.");
        }
    }

    private static DocumentBuilderFactory documentBuilderFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory;
    }

    private static Document getDocument(String INPUT_PATH) throws ParserConfigurationException,
            SAXException, IOException {
        Document doc = documentBuilderFactory().newDocumentBuilder().parse(INPUT_PATH);
        return doc;
    }

    private static void createOptionsList() {
        Option inputOption = new Option(Labels.INPUT_SHORT, Labels.INPUT, true,
                "XML input record.\n e.g. export_projekt.xml or folder in which "
                + "there are exports with current date to be processed");
        inputOption.setRequired(true);

        Option x3mlOption = new Option(Labels.X3ML_SHORT, Labels.X3ML, true,
                "X3ML mapping definition. \n e.g. AMCR_to_CIDOC_X3ML_projekt.xml\n");
        x3mlOption.setRequired(true);

        Option outputOption = new Option(Labels.OUTPUT_SHORT, Labels.OUTPUT, true,
                "The RDF output file name: \n e.g. rdf_projekt.rdf");
        outputOption.setRequired(true);

        Option keywordOption = new Option("kw", KEYWORD_OPTION_NAME, true,
                "valid keyword of the XML file, e.g projekt");
        keywordOption.setRequired(false);

        Option policyOption = new Option(Labels.POLICY_SHORT, Labels.POLICY, true,
                "The value policy file \n e.g. AMCR_Generator_Policy.xml");

        Option reportProgressOption = new Option(Labels.REPORT_PROGRESS_SHORT, Labels.REPORT_PROGRESS, false,
                "reports the progress of the transformations");

        Option transformAtOnce = new Option("aao", AAO_OPTION_NAME, false, "transforms the XML into RDF all at once");

        options.addOption(inputOption)
                .addOption(x3mlOption)
                .addOption(outputOption)
                .addOption(keywordOption)
                .addOption(policyOption)
                .addOption(reportProgressOption)
                .addOption(transformAtOnce);
    }

    private static void printTime(long a) {
        LOGGER.log(Level.INFO, "Total execution time: {0}s\n", Long.toString(a / 1000));
    }

    private static void printMemData() {
        int mb = 1024 * 1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }
}
