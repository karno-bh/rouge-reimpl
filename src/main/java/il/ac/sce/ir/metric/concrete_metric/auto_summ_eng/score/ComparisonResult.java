package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score;

import gr.demokritos.iit.jinsect.structs.GraphSimilarity;

import java.util.Objects;

public class ComparisonResult {

    private final GraphSimilarity overall;

    private final GraphSimilarity graph;

    private final GraphSimilarity histogram;

    public ComparisonResult(GraphSimilarity overall, GraphSimilarity graph, GraphSimilarity histogram) {
        this.overall = overall;
        this.graph = graph;
        this.histogram = histogram;
    }

    public GraphSimilarity getOverall() {
        return overall;
    }

    public GraphSimilarity getGraph() {
        return graph;
    }

    public GraphSimilarity getHistogram() {
        return histogram;
    }

    public static class Builder {

        private GraphSimilarity overall;

        private GraphSimilarity graph;

        private GraphSimilarity histogram;

        public Builder overall(GraphSimilarity overall) {
            this.overall = overall;
            return this;
        }

        public Builder graph(GraphSimilarity graph) {
            this.graph = graph;
            return this;
        }

        public Builder histogram(GraphSimilarity histogram) {
            this.histogram = histogram;
            return this;
        }

        public ComparisonResult build() {
            Objects.requireNonNull(overall);
            Objects.requireNonNull(graph);
            Objects.requireNonNull(histogram);

            return new ComparisonResult(overall, graph, histogram);
        }
    }
}
