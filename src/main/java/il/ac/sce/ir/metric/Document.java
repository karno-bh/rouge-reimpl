package il.ac.sce.ir.metric;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Document {

    private final String documentId;

    public Document(String documentId) {
        Objects.requireNonNull(documentId, "DocumentId cannot be null");
        if (documentId.isEmpty()) {
            throw new IllegalArgumentException("Document Id cannot be empty");
        }
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    private String text;

    private List<String> tokens;

    private Map<Integer, Map<String, Integer>> nGrams = new HashMap<>();

    public void setText(String text) {
        this.text = text;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public void setnGram(Map<String, Integer> nGrams, int nSize) {
        this.nGrams.put(nSize, nGrams);
    }

    public Map<String, Integer> getnGram(int nSize) {
        return this.nGrams.get(nSize);
    }


    public List<String> getTokens() {
        return tokens;
    }
}
