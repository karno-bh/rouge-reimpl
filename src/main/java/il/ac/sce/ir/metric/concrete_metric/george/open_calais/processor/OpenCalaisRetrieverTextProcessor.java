package il.ac.sce.ir.metric.concrete_metric.george.open_calais.processor;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Objects;

public class OpenCalaisRetrieverTextProcessor implements TextProcessor<String, String> {

    private final String calaisURL;

    private final int sleepTimeMS;

    private final int maxRetries;

    private final String apiKey;

    private final HttpClient httpClient;

    public OpenCalaisRetrieverTextProcessor(String calaisURL, int sleepTimeMS, String apiKey, HttpClient httpClient, int maxRetries) {
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

    private String postFile(File file, PostMethod method) {
        method.setRequestEntity(new FileRequestEntity(file, null));
        return doRequest(file, method);
    }

    private String doRequest(File file, PostMethod method) {
        try {
            int returnCode = httpClient.executeMethod(method);
            if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                //LOGGER.log(Level.SEVERE, "The Post method is not implemented by this URI");
                // still consume the response body
                method.getResponseBodyAsString();
            } else if (returnCode == HttpStatus.SC_OK) {
                //LOGGER.log(Level.FINE, "File post succeeded: " + file);

                //return saveResponse(file, method);

                return method.getResponseBodyAsString();

            } else {
//                LOGGER.log(Level.SEVERE, "File post failed: " + file);
//                LOGGER.log(Level.SEVERE, "Got code: " + returnCode);
//                LOGGER.log(Level.SEVERE, "response: " + method.getResponseBodyAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }

        return null;
    }

    private PostMethod createPostMethod() {
        PostMethod method = new PostMethod(calaisURL);

        method.setRequestHeader("X-AG-Access-Token", apiKey);   // Set mandatory parameters
        method.setRequestHeader("Content-Type", "text/raw");            // Set input content type
        method.setRequestHeader("outputformat", "application/json");    // Set response/output format

        return method;
    }

    @Override
    public Text<String> process(Text<String> data) {
        File input = new File(data.getTextId());

        String response = null;
        int numRetries = maxRetries;
        while (response == null) {
            response = postFile(input, createPostMethod());
            if (response == null) {
                if (numRetries < 0) {
                    try {
                        Thread.sleep(sleepTimeMS);
                    } catch (InterruptedException ignored) {}
                } else {
                    try {
                        Thread.sleep(sleepTimeMS);
                    } catch (InterruptedException ignored) {}
                    if (numRetries == 0) {
                        break;
                    } else {
                        numRetries--;
                    }
                }
            }
        }

        if (response == null) {
            throw new RuntimeException("Cannot get data from Open Calais");
        }
        return new Text<>(data.getTextId(), response);
    }
}
