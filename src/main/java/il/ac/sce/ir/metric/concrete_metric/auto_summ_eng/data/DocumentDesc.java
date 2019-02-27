package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.utils.StringUtils;

import java.text.MessageFormat;
import java.util.List;

public class DocumentDesc {

    private final int min;

    private final int max;

    private final int dist;

    private final String fileLocation;

    private final List<String> requiredFilters;

    private final TextProcessor<String, List<String>> filterTextProcessor;

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
        this.filterTextProcessor = null;
        this.requiredFilters = null;
    }

    public DocumentDesc(String fileLocation, int min, int max, int dist, List<String> requiredFilters, TextProcessor<String, List<String>> filterTextProcessor) {
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
        this.requiredFilters = requiredFilters;
        this.filterTextProcessor = filterTextProcessor;
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

    public List<String> getRequiredFilters() {
        return requiredFilters;
    }

    public TextProcessor<String, List<String>> getFilterTextProcessor() {
        return filterTextProcessor;
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
            List<String> requiredFilters = documentDesc.getRequiredFilters();
            String filterId = "";
            if (requiredFilters != null) {
                StringBuilder sb = new StringBuilder(64);
                int filterNum = 0;
                for (String requiredFilter : requiredFilters) {
                    if (filterNum == 0) {
                        sb.append(requiredFilter);
                    } else {
                        sb.append('_').append(requiredFilter);
                    }
                    filterNum++;
                }
                filterId = sb.toString();
            }
            String id = MessageFormat.format("{0}___{1}_{2}_{3}", documentDesc.getFileLocation(),
                    documentDesc.getMin(), documentDesc.getMax(), documentDesc.getDist()) + filterId;
            return new Text<>(
                    id,
                    documentDesc
            );
        }
    }
}
