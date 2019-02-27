package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor;

import gr.demokritos.iit.jinsect.documentModel.documentTypes.SimpleTextDocument;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.DocumentDesc;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.List;
import java.util.stream.Collectors;

public class JInsectSimpleTextDocumentTextProcessor implements TextProcessor<DocumentDesc, SimpleTextDocument> {

    @Override
    public Text<SimpleTextDocument> process(Text<DocumentDesc> data) {

        DocumentDesc documentDesc = data.getTextData();
        int wordMin = documentDesc.getMin();
        int wordMax = documentDesc.getMax();
        int wordDist = documentDesc.getDist();
        String fileLocation = documentDesc.getFileLocation();
        SimpleTextDocument simpleTextDocument = new SimpleTextDocument(wordMin, wordMax, wordDist);
        if (documentDesc.getRequiredFilters() == null || documentDesc.getRequiredFilters().isEmpty()) {
            simpleTextDocument.loadDataStringFromFile(fileLocation);
        } else {
            TextProcessor<String, List<String>> filterTextProcessor = documentDesc.getFilterTextProcessor();
            Text<String> fileText = Text.asFileLocation(fileLocation);
            Text<List<String>> filteredTokens = filterTextProcessor.process(fileText);
            String filteredText = filteredTokens.getTextData().stream().collect(Collectors.joining(" "));
            simpleTextDocument.setDataString(filteredText);
        }


        return new Text<>(
                data.getTextId(),
                simpleTextDocument
        );
    }

}
