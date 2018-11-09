package il.ac.sce.ir.metric.core.reporter.file_system_reflection;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class ProcessedSystem {

    private final String dirLocation;
    private final String description;


    private ProcessedSystem(String dirLocation, String description) {
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

        public ProcessedSystem build() {
            return new ProcessedSystem(dirLocation, description);
        }
    }
}
