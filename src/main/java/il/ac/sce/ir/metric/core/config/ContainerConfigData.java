package il.ac.sce.ir.metric.core.config;

import java.util.List;
import java.util.Map;

public class ContainerConfigData {

    private static final Utils utils = new Utils();

    private final String fileSystemCachePath;

    private final List<String> topics;

    private ContainerConfigData(Mirror mirror) {
        this.fileSystemCachePath = mirror.fileSystemCachePath;
        this.topics = mirror.topics;
    }

    public String getFileSystemCachePath() {
        return fileSystemCachePath;
    }

    public List<String> getTopics() {
        return topics;
    }

    public static ContainerConfigData fromMap(Map<String, Object> jsonParsedMap) {
        Mirror mirror = new Mirror();
        mirror.fileSystemCachePath = utils.requireJSONTypeAndCast(jsonParsedMap.get(Constants.FILE_SYSTEM_CACHE_PATH),
                Constants.FILE_SYSTEM_CACHE_PATH, String.class);
        mirror.topics = utils.requireJSONTypeAndCast(jsonParsedMap.get(Constants.ORIGINAL_TOPICS), Constants.ORIGINAL_TOPICS, List.class);

        return new ContainerConfigData(mirror);
    }

    private static final class Mirror {
        private String fileSystemCachePath;
        private List<String> topics;
    }
}
