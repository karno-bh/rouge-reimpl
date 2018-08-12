package il.ac.sce.ir.metric.core.processor.token_filter;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.ArrayList;
import java.util.List;

public class LowerCaseFilterProcessor implements TextProcessor<List<String>, List<String>> {

    @Override
    public Text<List<String>> process(Text<List<String>> data) {
        List<String> filteredTokens = new ArrayList<>();
        for (String token : data.getTextData()) {
            filteredTokens.add(token.toLowerCase());
        }
        return new Text<>(data.getTextId(), filteredTokens);
    }
}
