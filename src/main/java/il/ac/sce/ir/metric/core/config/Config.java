package il.ac.sce.ir.metric.core.config;

public class Config {

    private static final String CONFIG_FILE = "config.json";

    private static volatile Config instance;

    private String modelsPath;

    private String peersPath;

    public String getModelsPath() {
        return modelsPath;
    }

    public String getPeersPath() {
        return peersPath;
    }

    private Config() {}

    public static Config getInstance(String configFileName) {
        String realConfigFileName = CONFIG_FILE;
        if (configFileName == null || configFileName.isEmpty()) {
            realConfigFileName = configFileName;
        }
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = loadConfig(realConfigFileName);
                }
            }
        }
        return instance;
    }

    private static Config loadConfig(String configFlle) {
        return null;
    }
}
