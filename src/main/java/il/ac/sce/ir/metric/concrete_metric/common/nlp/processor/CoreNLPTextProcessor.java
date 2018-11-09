package il.ac.sce.ir.metric.concrete_metric.common.nlp.processor;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class CoreNLPTextProcessor implements TextProcessor<String, Annotation> {

    private final Supplier<StanfordCoreNLP> pipeline;

    private final AtomicReference<StanfordCoreNLP> cachedPipeline = new AtomicReference<>();

    public CoreNLPTextProcessor(Supplier<StanfordCoreNLP> pipeline) {
        Objects.requireNonNull(pipeline, "Core NLP Pipeline Supplier cannot be null");
        this.pipeline = pipeline;
    }

    @Override
    public Text<Annotation> process(Text<String> data) {
        Annotation document = new Annotation(data.getTextData());
        StanfordCoreNLP realPipeline = cachedPipeline.get();
        if (realPipeline == null ) {
            realPipeline = pipeline.get();
            cachedPipeline.set(realPipeline);
        }
        realPipeline.annotate(document);
        return new Text<>(data.getTextId(), document);
    }
}
