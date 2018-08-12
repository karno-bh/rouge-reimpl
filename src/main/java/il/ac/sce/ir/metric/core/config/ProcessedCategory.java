package il.ac.sce.ir.metric.core.config;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class ProcessedCategory {

    private final String dirLocation;
    private final String description;

    private ProcessedCategory(String dirLocation, String description) {
        Objects.requireNonNull(dirLocation, "Directory Location cannot be null");
        this.dirLocation = dirLocation;
        this.description = description;
    }

    public String getDirLocation() {
        return dirLocation;
    }

    public String getDescription() {
        if (StringUtils.isEmpty(description)) {
            return getDirLocation();
        }
        return description;
    }

    public static Builder as() {
        return new Builder();
    }

    public static class Builder {
        private String dirLocation;
        private String description;

        public Builder dirLocation(String dirLocation) {
            this.dirLocation = dirLocation;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public ProcessedCategory build() {
            return new ProcessedCategory(dirLocation, description);
        }
    }

}
