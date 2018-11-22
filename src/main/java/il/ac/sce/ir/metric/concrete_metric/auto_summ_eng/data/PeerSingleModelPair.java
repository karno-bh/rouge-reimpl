package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data;

public class PeerSingleModelPair {

    private final DocumentDesc peer;

    private final DocumentDesc model;

    public PeerSingleModelPair(DocumentDesc peer, DocumentDesc model) {
        this.peer = peer;
        this.model = model;
    }

    public DocumentDesc getPeer() {
        return peer;
    }

    public DocumentDesc getModel() {
        return model;
    }
}
