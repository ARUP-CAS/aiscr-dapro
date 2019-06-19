package cz.ssc.scrub;

import cz.ssc.filter.AccessLevel;
import cz.ssc.filter.RDFFilter;
import cz.ssc.filter.TagNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Scrubber {
    private static final Logger LOGGER = Logger.getLogger(Scrubber.class.getName());

    private final DocumentBuilder documentBuilder;

    private final Transformer transformer;

    public static void main(String[] args) {
        LOGGER.setLevel(Level.INFO);

        try {
            Options options = new Options();
            options.addOption(makeOption("input", "XML input.\nEither file, or directory with input files."));
            options.addOption(makeOption("output", "XML output directory."));
            CommandLineParser parser = new DefaultParser();
            CommandLine cli = parser.parse(options, args);
            String inputVal = cli.getOptionValue("input");
            String outputVal = cli.getOptionValue("output");
            run(inputVal, outputVal);
            System.exit(0);
        } catch (ParseException ex) {
            LOGGER.log(Level.SEVERE, "invalid command line", ex);
            System.exit(2);
        } catch (IOException | ParserConfigurationException | SAXException | TagNotFoundException | TransformerException ex) {
            LOGGER.log(Level.SEVERE, "scrubbing failed", ex);
            System.exit(2);
        }
    }
    
    static void run(String inputVal, String outputVal)
            throws ParserConfigurationException, SAXException, IOException,
                TagNotFoundException, TransformerConfigurationException, TransformerException {
        final Path inputPath = Paths.get(inputVal);
        File outputFile = new File(outputVal);
        final Path outputPath = Paths.get(outputVal);
        outputFile.mkdirs();
        
        final Scrubber scrubber = new Scrubber();
        if (Files.isRegularFile(inputPath)) {
            scrubber.produceAll(inputPath, outputPath);
        } else if (Files.isDirectory(inputPath)) {
            final PathMatcher matcher = FileSystems.getDefault().getPathMatcher(makeGlob());
            Files.walkFileTree(inputPath, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.equals(inputPath)) {
                        return FileVisitResult.CONTINUE;
                    } else {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path name = file.getFileName();
                    if ((name != null) && matcher.matches(name)) {
                        try {
                            scrubber.produceAll(file, outputPath);
                        } catch (SAXException | TagNotFoundException | TransformerException ex) {
                            LOGGER.log(Level.WARNING, "scrubbing failed for " + file.toString());
                            throw new IOException(ex);
                        }
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    throw exc;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null) {
                        throw exc;
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            throw new FileNotFoundException();
        }
    }
    
    Scrubber() throws ParserConfigurationException, 
            TransformerConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        documentBuilder = dbf.newDocumentBuilder();
        
        TransformerFactory tf = TransformerFactory.newInstance();
        transformer = tf.newTransformer();
    }

    void produceAll(Path inputFile, Path outputDir) 
            throws SAXException, IOException, TagNotFoundException, 
                TransformerException {
        Path basePath = inputFile.getFileName();
        String baseName = basePath.toString();
        // admin has no scrubbed file - they see all
        for (char access = 'A'; access <= 'D'; ++access) {
            String level = Character.toString(access);
            String outputName = baseName.replace("amcr", level);
            Path outputPath = outputDir.resolve(outputName);
            produceOne(inputFile, outputPath, AccessLevel.convert(level));
        }
    }

    void produceOne(Path inputFile, Path outputFile, AccessLevel level)
            throws SAXException, IOException, TagNotFoundException, TransformerException {
        LOGGER.log(Level.INFO, String.format("Scrubbing %s for level %s...", inputFile.toString(), level));
        FileInputStream inputStream = new FileInputStream(inputFile.toFile());
        Document doc = documentBuilder.parse(new InputSource(inputStream));

        Element root = doc.getDocumentElement();
        Node prev = root.getFirstChild();
        while (prev != null) {
            Node next = prev.getNextSibling();
            if ((prev.getNodeType() != Node.ELEMENT_NODE) ||
                    !RDFFilter.filter(prev, level)) {
                root.removeChild(prev);
            }

            prev = next;
        }

        DOMSource source = new DOMSource(doc);
        StreamResult dest = new StreamResult(outputFile.toFile());
        transformer.transform(source, dest);
    }

    static Option makeOption(String name, String desc) {
        Option opt = new Option(name.substring(0, 1), name, true, desc);
        opt.setRequired(true);
        return opt;
    }
    
    static String makeGlob() {
        StringBuilder sb = new StringBuilder();
        sb.append("glob:");
        
        sb.append("export_");
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        sb.append(dayFormat.format(new Date()));
        sb.append("_*_amcr.xml");
        return sb.toString();
    }
}
