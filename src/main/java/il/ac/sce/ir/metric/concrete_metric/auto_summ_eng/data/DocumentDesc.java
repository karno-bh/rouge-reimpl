package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.utils.StringUtils;

import java.text.MessageFormat;

public class DocumentDesc {

    private final int min;

    private final int max;

    private final int dist;

    private final String fileLocation;

    public DocumentDesc(String fileLocation, int min, int max, int dist) {
        if (new StringUtils().isEmpty(fileLocation)) {
            throw new IllegalArgumentException("File Location is Empty");
        }
        if (min < 0) {
            throw new IllegalArgumentException("Min should be greater than zero");
        }
        if (max < 0) {
            throw new IllegalArgumentException("Max should be greater than zero");
        }
        if (dist < 0) {
            throw new IllegalArgumentException("dist should be greater than zero");
        }

        this.fileLocation = fileLocation;
        this.min = min;
        this.max = max;
        this.dist = dist;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getDist() {
        return dist;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public static class TextBuilder {

        public Text<DocumentDesc> buildText(String fileLocation, int min, int max, int dist) {
            DocumentDesc documentDesc = new DocumentDesc(fileLocation, min, max, dist);
            return new Text<>(
                    MessageFormat.format("{0}___{1}_{2}_{3}", fileLocation, min, max, dist),
                    documentDesc
            );
        }

        public Text<DocumentDesc> buildText(DocumentDesc documentDesc) {
            return new Text<>(
                    MessageFormat.format("{0}___{1}_{2}_{3}", documentDesc.getFileLocation(),
                            documentDesc.getMin(), documentDesc.getMax(), documentDesc.getDist()),
                    documentDesc
            );
        }
    }
}
