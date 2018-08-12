package il.ac.sce.ir.metric.core.data;

import java.util.Objects;

public class Text<T> {

    private final String textId;
    private final T textData;

    public Text(String textId, T textData) {
        Objects.requireNonNull(textId, "Text Id cannot be null");
        Objects.requireNonNull(textData, "Text data cannot be null");
        this.textId = textId;
        this.textData = textData;
    }

    public String getTextId() {
        return textId;
    }

    public T getTextData() {
        return textData;
    }

    public static Text<String> asFileLocation(String textId) {
        return new Text<String>(textId, textId);
    }
}
