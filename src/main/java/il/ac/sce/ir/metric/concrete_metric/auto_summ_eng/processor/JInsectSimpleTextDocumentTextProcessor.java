package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor;

import gr.demokritos.iit.jinsect.documentModel.documentTypes.SimpleTextDocument;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.DocumentDesc;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

public class JInsectSimpleTextDocumentTextProcessor implements TextProcessor<DocumentDesc, SimpleTextDocument> {

    @Override
    public Text<SimpleTextDocument> process(Text<DocumentDesc> data) {

        DocumentDesc documentDesc = data.getTextData();
        int wordMin = documentDesc.getMin();
        int wordMax = documentDesc.getMax();
        int wordDist = documentDesc.getDist();
        String fileLocation = documentDesc.getFileLocation();

        SimpleTextDocument simpleTextDocument = new SimpleTextDocument(wordMin, wordMax, wordDist);
        simpleTextDocument.loadDataStringFromFile(fileLocation);

        return new Text<>(
                data.getTextId(),
                simpleTextDocument
        );
    }

}
