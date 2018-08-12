package il.ac.sce.ir.metric;

import entity_extractor.TextEntities;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class OpenCalaisExtractorProcessor implements TextProcessor<String, TextEntities> {

    private static final String DEFAULT_URL = "https://api.thomsonreuters.com/permid/calais";

    private static final int DEFAULT_SLEEP_TIME_MS = 500;

    private static final int DEFAULT_MAX_RETRIES = 10;

    private static final String[] DEFAULT_HTTP_CLIENT_USER_AGENT = {"http.useragent", "Calais Rest Client"};


    private final String calaisURL;

    private final int sleepTimeMS;

    private final int maxRetries;

    private final String apiKey;

    private final HttpClient httpClient;

    public OpenCalaisExtractorProcessor(String calaisURL, int sleepTimeMS, String apiKey, HttpClient httpClient, int maxRetries) {
        if (StringUtils.isEmpty(calaisURL)) {
            throw new IllegalArgumentException("Open Calais URL cannot be empty");
        }

        if (sleepTimeMS < 0) {
            throw new IllegalArgumentException("Sleep time cannot be negative");
        }

        if (StringUtils.isEmpty(apiKey)) {
            throw new IllegalArgumentException("API Key cannot be empty");
        }

        Objects.requireNonNull(httpClient, "HTTP Client cannot be empty");

        this.calaisURL  = calaisURL;
        this.sleepTimeMS = sleepTimeMS;
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.maxRetries = maxRetries;
    }

    @Override
    public Text<TextEntities> process(Text<String> data) {
        return null;
    }
}
