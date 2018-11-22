package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor;

import gr.demokritos.iit.jinsect.documentModel.documentTypes.NGramSymWinDocument;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.DocumentDesc;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

public class JInsectNGramSymWinDocumentTextProcessor implements TextProcessor<DocumentDesc, NGramSymWinDocument> {

    @Override
    public Text<NGramSymWinDocument> process(Text<DocumentDesc> data) {

        DocumentDesc documentDesc = data.getTextData();
        int charMin = documentDesc.getMin();
        int charMax = documentDesc.getMax();
        int charDist = documentDesc.getDist();
        String fileLocation = documentDesc.getFileLocation();

        NGramSymWinDocument nGramSymWinDocument = new NGramSymWinDocument(charMin, charMax, charDist, charMin, charMax);
        nGramSymWinDocument.loadDataStringFromFile(fileLocation);

        return new Text<>(
                data.getTextId(),
                nGramSymWinDocument
        );
    }
}
