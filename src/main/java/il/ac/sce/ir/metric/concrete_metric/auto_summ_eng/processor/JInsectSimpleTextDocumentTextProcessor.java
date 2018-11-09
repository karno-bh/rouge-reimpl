package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor;

import gr.demokritos.iit.jinsect.documentModel.documentTypes.SimpleTextDocument;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

public class JInsectSimpleTextDocumentTextProcessor implements TextProcessor<String, SimpleTextDocument> {

    private final int wordMin;

    private final int wordMax;

    private final int wordDist;

    public JInsectSimpleTextDocumentTextProcessor(int wordMin, int wordMax, int wordDist) {
        if (wordMin < 0) {
            throw new IllegalArgumentException("WordMin should be greater than zero");
        }
        if (wordMax < 0) {
            throw new IllegalArgumentException("WordMax should be greater than zero");
        }
        if (wordDist < 0) {
            throw new IllegalArgumentException("WordMax should be greater than zero");
        }
        this.wordMin = wordMin;
        this.wordMax = wordMax;
        this.wordDist = wordDist;
    }

    @Override
    public Text<SimpleTextDocument> process(Text<String> data) {

        SimpleTextDocument simpleTextDocument = new SimpleTextDocument(wordMin, wordMax, wordDist);
        simpleTextDocument.loadDataStringFromFile(data.getTextData());

        return new Text<>(
                data.getTextId(),
                simpleTextDocument
        );
    }

    public int getWordMin() {
        return wordMin;
    }

    public int getWordMax() {
        return wordMax;
    }

    public int getWordDist() {
        return wordDist;
    }
}
