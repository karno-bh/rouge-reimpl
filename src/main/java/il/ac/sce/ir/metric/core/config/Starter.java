package il.ac.sce.ir.metric.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Starter implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(Starter.class);
    private final static Utils utils = new Utils();

    private final String[] args;
    private final Map<String, String> parsedArgs;
    private final Configuration configuration;


    public Starter(String[] args) {
        this.args = args;
        this.parsedArgs = parseArgs(args);
        if (parsedArgs.keySet().contains(Constants.HELP_FILE_KEY)) {
            utils.printHelpAndExitProcess();
        }
        this.configuration = Configuration.fromFileName(parsedArgs.get(Constants.CONFIG_FILE_KEY));
    }

    public static void main(String[] args) {
        try {
            Runnable starter = new Starter(args);
            starter.run();
        } catch (Throwable t) {
            System.err.println("Exception occurred during execution");
            log.error("Fatal program error", t);
        }
    }

    @Override
    public void run() {
        try {
            Class<Container> containerClass = (Class<Container>)Class.forName(configuration.getContainerClass());
            Class<MainAlgo> mainAlgoClass = (Class<MainAlgo>)Class.forName(configuration.getMainAlgoClass());

            Container container = containerClass.newInstance();
            container.setConfiguration(configuration);
            container.build();
            MainAlgo mainAlgo = mainAlgoClass.newInstance();

            mainAlgo.setContainer(container);
            mainAlgo.run();
        } catch (Exception e) {
            throw new RuntimeException("Error while running the process", e);
        }
    }

    private Map<String, String> parseArgs(String[] args) {
        Map<String, String> parsedArgs = new HashMap<>();
        parsedArgs.put(Constants.CONFIG_FILE_KEY, Constants.DEFAULT_CONFIG_FILE);
        for (int i = 0; i < args.length; i+=2) {
            String key = args[i];
            String value = "";
            if (i + 1 != args.length) {
                value = args[i + 1];
            }
            parsedArgs.put(key, value);
        }
        return  parsedArgs;
    }
}
