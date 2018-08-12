package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.Text;

import java.util.Arrays;
import java.util.List;

public class TextToTokensProcessor implements TextProcessor<String, List<String>> {

    @Override
    public Text<List<String>> process(Text<String> data) {
        String text = data.getTextData();
        String[] split = text.split("\\s+");
        List<String> tokens = Arrays.asList(split);

        return new Text<>(data.getTextId(), tokens);
    }
}
