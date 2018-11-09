package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor;

import gr.demokritos.iit.jinsect.documentModel.documentTypes.NGramSymWinDocument;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

public class JInsectNGramSymWinDocumentTextProcessor implements TextProcessor<String, NGramSymWinDocument> {

    private final int charMin;

    private final int charMax;

    private final int charDist;

    public JInsectNGramSymWinDocumentTextProcessor(int charMin, int charMax, int charDist) {
        if (charMin < 0) {
            throw new IllegalArgumentException("CharMin should be greater than zero");
        }
        if (charMax < 0) {
            throw new IllegalArgumentException("CharMax should be greater than zero");
        }
        if (charDist < 0) {
            throw new IllegalArgumentException("CharMax should be greater than zero");
        }

        this.charMin = charMin;
        this.charMax = charMax;
        this.charDist = charDist;
    }

    @Override
    public Text<NGramSymWinDocument> process(Text<String> data) {

        NGramSymWinDocument nGramSymWinDocument = new NGramSymWinDocument(charMin, charMax, charDist, charMin, charMax);
        nGramSymWinDocument.loadDataStringFromFile(data.getTextData());

        return new Text<>(
                data.getTextId(),
                nGramSymWinDocument
        );
    }
}
