package il.ac.sce.ir.metric.starter.gui.main.resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static il.ac.sce.ir.metric.starter.gui.main.resources.GUIConstants.*;

public class DefaultGUIMessages {

    private static final Map<String, String> MESSAGES = Collections.unmodifiableMap(
      new HashMap<String, String>() {{
          put(MAIN_TITLE_NAME, "Summary Evaluator");
          put(SECTION_RUN_METRICS, "Run Metrics");
          put(SECTION_ANALYZE_RESULTS, "Analyze Results");

      }}
    );

    public Map<String, String> getMessages() {
        return MESSAGES;
    }
}
