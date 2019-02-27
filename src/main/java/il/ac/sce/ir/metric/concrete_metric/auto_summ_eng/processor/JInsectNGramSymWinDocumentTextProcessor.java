package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor;

import gr.demokritos.iit.jinsect.documentModel.documentTypes.NGramSymWinDocument;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.DocumentDesc;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.List;
import java.util.stream.Collectors;

public class JInsectNGramSymWinDocumentTextProcessor implements TextProcessor<DocumentDesc, NGramSymWinDocument> {

    @Override
    public Text<NGramSymWinDocument> process(Text<DocumentDesc> data) {

        DocumentDesc documentDesc = data.getTextData();
        int charMin = documentDesc.getMin();
        int charMax = documentDesc.getMax();
        int charDist = documentDesc.getDist();
        String fileLocation = documentDesc.getFileLocation();
        NGramSymWinDocument nGramSymWinDocument = new NGramSymWinDocument(charMin, charMax, charDist, charMin, charMax);

        if (documentDesc.getRequiredFilters() == null && documentDesc.getRequiredFilters().isEmpty()) {
            nGramSymWinDocument.loadDataStringFromFile(fileLocation);
        } else {
            TextProcessor<String, List<String>> filterTextProcessor = documentDesc.getFilterTextProcessor();
            Text<String> fileText = Text.asFileLocation(fileLocation);
            Text<List<String>> filteredTokens = filterTextProcessor.process(fileText);
            String filteredText = filteredTokens.getTextData().stream().collect(Collectors.joining(" "));
            nGramSymWinDocument.setDataString(filteredText);
        }



        return new Text<>(
                data.getTextId(),
                nGramSymWinDocument
        );
    }

}
