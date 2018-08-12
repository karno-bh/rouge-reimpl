package il.ac.sce.ir.metric.core.processor.token_filter;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PunctuationFilterProcessor implements TextProcessor<List<String>, List<String>> {

    @Override
    public Text<List<String>> process(Text<List<String>> data) {
        List<String> filteredTokens = new ArrayList<>();
        Pattern p = Pattern.compile("[^\\\\dA-Za-z0-9 ]");


        for (String token : data.getTextData()) {
            String filtered = p.matcher(token).replaceAll("");
            if (!filtered.isEmpty()) {
                filteredTokens.add(filtered);
            }
        }

        return new Text<>(data.getTextId(), filteredTokens);
    }
}
