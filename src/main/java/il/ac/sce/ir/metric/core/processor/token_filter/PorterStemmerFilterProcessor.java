package il.ac.sce.ir.metric.core.processor.token_filter;

import edu.northwestern.at.morphadorner.corpuslinguistics.stemmer.PorterStemmer;
import edu.northwestern.at.morphadorner.corpuslinguistics.stemmer.Stemmer;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.ArrayList;
import java.util.List;

public class PorterStemmerFilterProcessor implements TextProcessor<List<String>, List<String>> {

    @Override
    public Text<List<String>> process(Text<List<String>> data) {
        List<String> filteredTokens = new ArrayList<>();
        Stemmer stemmer = new PorterStemmer();

        for (String token : data.getTextData()) {
            String stemmed = stemmer.stem(token);
            filteredTokens.add(stemmed);
        }

        return new Text<>(data.getTextId(), filteredTokens);
    }
}
