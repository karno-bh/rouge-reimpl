package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileToStringProcessor implements TextProcessor<String, String> {

    @Override
    public Text<String> process(Text<String> data) {
        File textFile = new File(data.getTextData());
        String text = null;
        try {
            Scanner scanner = new Scanner(textFile);
            scanner.useDelimiter("\\Z");
            text = scanner.next();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error while loading " + data.getTextId(), e);
        }
        Text<String> textData = new Text<>(data.getTextId(), text);
        return textData;
    }
}
