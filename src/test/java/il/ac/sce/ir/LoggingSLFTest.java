package il.ac.sce.ir;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingSLFTest {

    //private static final Logger LOG = LoggerFactory.getLogger(LoggingSLFTest.class);

    @BeforeClass
    public static void setUp() {
        String path = LoggingSLFTest.class.getClassLoader()
                .getResource("jul-log.properties")
                .getFile();
        System.setProperty("java.util.logging.config.file", path);
    }

    @Test
    public void loggingBySLFTest() {
        Logger LOG = LoggerFactory.getLogger(LoggingSLFTest.class);

        LOG.info("Hello Logger");

        try {
            throw new NullPointerException("This is a test");
        } catch (Exception e) {
            LOG.error("Caught some exception", e);
        }

        LOG.debug("This is a debug print");

        LOG.warn("This is a warning print!");
    }
}
