package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data;

public class SimpleTextNGramPair {

    private final PeerSingleModelPair simpleTextPair;

    private final PeerSingleModelPair nGramPair;

    public PeerSingleModelPair getSimpleTextPair() {
        return simpleTextPair;
    }

    public PeerSingleModelPair getnGramPair() {
        return nGramPair;
    }

    public SimpleTextNGramPair(PeerSingleModelPair simpleTextPair, PeerSingleModelPair nGramPair) {
        this.simpleTextPair = simpleTextPair;
        this.nGramPair = nGramPair;
    }
}
