package il.ac.sce.ir;

import entity_extractor.TextEntities;
import il.ac.sce.ir.metric.namedentitygraph.utils.OpenCalaisExtractorCustom;
import org.junit.Test;

import java.io.File;

public class OpenCalaisExtractorCustomTest {

    @Test
    public void openCalaisCustomTest() {
        OpenCalaisExtractorCustom openCalaisExtractorCustom = OpenCalaisExtractorCustom.asDefault().apiKey("iSDDWuAd3qriwV0UYpkepAXjgamaI8BE").build();
        File file = new File("c:/my/temp/sample.txt");
        TextEntities entities = openCalaisExtractorCustom.getEntities(file);
        System.out.println(entities);

        System.out.println(entities.getText());
        entities.getEntities().forEach(System.out::println);
    }
}
