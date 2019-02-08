package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileToStringProcessor implements TextProcessor<String, String> {

    private final Logger logger = LoggerFactory.getLogger(FileToStringProcessor.class);

    @Override
    public Text<String> process(Text<String> data) {
        // File textFile = new File(data.getTextData());
        String text = null;
        try {
            // logger.info("Processing file {}", data.getTextData());
            /*Scanner scanner = new Scanner(textFile);
            scanner.useDelimiter("\\Z");
            text = scanner.next();*/
            byte[] encoded = Files.readAllBytes(Paths.get(data.getTextData()));
            text = new String(encoded, StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error while loading " + data.getTextId(), e);
        } catch (Exception e) {
            logger.error("Got exception on file {}", data.getTextData(), e);
        }
        Text<String> textData = new Text<>(data.getTextId(), text);
        return textData;
    }
}
