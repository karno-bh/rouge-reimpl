package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score_calculator;

import gr.demokritos.iit.jinsect.documentModel.comparators.StandardDocumentComparator;
import gr.demokritos.iit.jinsect.documentModel.documentTypes.SimpleTextDocument;
import gr.demokritos.iit.jinsect.structs.GraphSimilarity;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.DocumentDesc;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.PeerSingleModelPair;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score.ComparisonResult;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.io.InvalidClassException;


//TODO combine with AutoSummENGNGramScoreCalculator
public class AutoSummENGSimpleTextScoreCalculator implements AutoSummENGScoreCalculator {

    private final TextProcessor<DocumentDesc, SimpleTextDocument> textProcessor;

    public AutoSummENGSimpleTextScoreCalculator(TextProcessor<DocumentDesc, SimpleTextDocument> textProcessor) {
        this.textProcessor = textProcessor;
    }

    @Override
    public ComparisonResult computeScore(PeerSingleModelPair peerSingleModelPair) {

        System.out.println("AutoSummENGSimpleTextScoreCalculator start");
        DocumentDesc.TextBuilder textBuilder = new DocumentDesc.TextBuilder();

        DocumentDesc peer = peerSingleModelPair.getPeer();
        Text<DocumentDesc> peerText = textBuilder.buildText(peer);
        Text<SimpleTextDocument> peerSimpleTextDocumentText = textProcessor.process(peerText);
        SimpleTextDocument peerSimpleTextDocument = peerSimpleTextDocumentText.getTextData();

        DocumentDesc model = peerSingleModelPair.getModel();
        Text<DocumentDesc> modelText = textBuilder.buildText(model);
        Text<SimpleTextDocument> modelSimpleTextDocumentText = textProcessor.process(modelText);
        SimpleTextDocument modelSimpleTextDocument = modelSimpleTextDocumentText.getTextData();

        StandardDocumentComparator comparator = new StandardDocumentComparator();
        GraphSimilarity overallGraphSimilarity;
        try {
            overallGraphSimilarity = comparator.getSimilarityBetween(peerSimpleTextDocument, modelSimpleTextDocument);
        } catch (InvalidClassException e) {
            throw new IllegalArgumentException("Error while calculating", e);
        }

        System.out.println("AutoSummENGSimpleTextScoreCalculator end");
        return new ComparisonResult.Builder()
                .overall(overallGraphSimilarity)
                .graph(comparator.getGraphSimilarity())
                .histogram(comparator.getHistogramSimilarity())
                .build();

    }
}
