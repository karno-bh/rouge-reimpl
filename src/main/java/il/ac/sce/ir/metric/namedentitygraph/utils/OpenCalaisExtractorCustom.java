package il.ac.sce.ir.metric.namedentitygraph.utils;

import entity_extractor.EntityExtractor;
import entity_extractor.ExtractedEntity;
import entity_extractor.TextEntities;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Methods;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;

public class OpenCalaisExtractorCustom implements EntityExtractor {

    private static final String DEFAULT_URL = "https://api.thomsonreuters.com/permid/calais";

    private static final int DEFAULT_SLEEP_TIME_MS = 500;

    private static final int DEFAULT_MAX_RETRIES = 10;

    private static final String[] DEFAULT_HTTP_CLIENT_USER_AGENT = {"http.useragent", "Calais Rest Client"};


    private final String calaisURL;

    private final int sleepTimeMS;

    private final int maxRetries;

    private final String apiKey;

    private final HttpClient httpClient;


    public OpenCalaisExtractorCustom(String calaisURL, int sleepTimeMS, String apiKey, HttpClient httpClient, int maxRetries) {
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


    private PostMethod createPostMethod() {
        PostMethod method = new PostMethod(calaisURL);

        method.setRequestHeader("X-AG-Access-Token", apiKey);   // Set mandatory parameters
        method.setRequestHeader("Content-Type", "text/raw");            // Set input content type
        method.setRequestHeader("outputformat", "application/json");    // Set response/output format

        return method;
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

    private TextEntities getEntitiesFromOpenCalaisResponse(String responseStr) {
        TextEntities entities = new TextEntities();
        JSONObject obj = new JSONObject(responseStr);

        // Get the document text from the response
        String text = obj.getJSONObject("doc").getJSONObject("info").getString("document");
        entities.setText(text);

        boolean alwaysExtract = true;
        // Extract the entities from the response, only if a method which uses them is enabled
        if (alwaysExtract || Methods.isEnabled(Methods.PLACEHOLDER) || Methods.isEnabled(Methods.PLACEHOLDER_SS)
                || Methods.isEnabled(Methods.RANDOM) || Methods.isEnabled(Methods.PLACEHOLDER_EXTRA_WEIGHT)) {
            // Create blacklist with names of entities to ignore
            ArrayList<String> blacklist = new ArrayList<>();
            blacklist.add("DEV");
            blacklist.add("http");
            blacklist.add("html");

            // Get entities
            Iterator<String> tags = obj.keys();
            while (tags.hasNext()) {
                String tagID = tags.next();

                if (tagID.equals("doc")) {
                    continue;
                }

                JSONObject tag = obj.getJSONObject(tagID);

                // Check that object has at least the basic properties that we need to continue
                if (!tag.has("_typeGroup") || !tag.has("name") || !tag.has("_type") || !tag.has("instances")) {
                    continue;
                }

                // If object is not of type "entities", ignore it as we only want entities
                String typeGroup = tag.getString("_typeGroup");
                if (!typeGroup.equals("entities")) {
                    continue;
                }

                // Skip tags that are not for end user display
//                if (tag.getString("forenduserdisplay").equals("false")) {
//                    continue;
//                }

                // Get all of this entity's instances
                JSONArray instances = tag.getJSONArray("instances");

                for (Object instance : instances) {
                    // Check that the object is indeed a JSONObject before casting it
                    if (!(instance instanceof JSONObject)) {
                        ///LOGGER.log(Level.SEVERE, "Instance is not a JSONObject! Skipping it...");
                        continue;
                    }

                    // Cast instance to a JSONObject to continue
                    JSONObject i = (JSONObject) instance;

                    if (!i.has("offset") || !i.has("length")) {
                        //LOGGER.log(Level.SEVERE, "Instance does not have offset or length! Skipping it...");
                        continue;
                    }

                    // Initialize entity for this instance and add the required properties to it
                    ExtractedEntity entity = new ExtractedEntity();

//                    entity.setName(i.getString("exact"));
                    entity.setName(tag.getString("name"));
                    entity.setType(tag.getString("_type"));
                    entity.setOffset(i.getInt("offset"));
                    entity.setLength(i.getInt("length"));

                    // Only add entity if its name is not blacklisted
                    if (!blacklist.contains(entity.getName())) {
                        entities.addEntity(entity);
                    }
                }
            }
        }

        return entities;
    }

    @Override
    public TextEntities getEntities(File input) {
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

        TextEntities entities = getEntitiesFromOpenCalaisResponse(response);
        entities.setTitle(input.getName());
        return entities;
    }

    public static OpenCalaisExtractorCustomBuilder as() {
        return new OpenCalaisExtractorCustomBuilder();
    }

    public static OpenCalaisExtractorCustomBuilder asDefault() {
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter(DEFAULT_HTTP_CLIENT_USER_AGENT[0], DEFAULT_HTTP_CLIENT_USER_AGENT[1]);
        OpenCalaisExtractorCustomBuilder builder = new OpenCalaisExtractorCustomBuilder();
        builder.calaisURL(DEFAULT_URL)
                .sleepTimeMS(DEFAULT_SLEEP_TIME_MS)
                .httpClient(httpClient)
                .maxRetries(DEFAULT_MAX_RETRIES);
        return builder;
    }

    public static class OpenCalaisExtractorCustomBuilder {

        private String calaisURL;

        private int sleepTimeMS;

        private String apiKey;

        private HttpClient httpClient;

        private int maxRetries;

        public OpenCalaisExtractorCustomBuilder calaisURL(String calaisURL) {
            this.calaisURL = calaisURL;
            return this;
        }

        public OpenCalaisExtractorCustomBuilder sleepTimeMS(int sleepTimeMS) {
            this.sleepTimeMS = sleepTimeMS;
            return this;
        }

        public OpenCalaisExtractorCustomBuilder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public OpenCalaisExtractorCustomBuilder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public OpenCalaisExtractorCustomBuilder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public OpenCalaisExtractorCustom build() {
            return new OpenCalaisExtractorCustom(calaisURL, sleepTimeMS, apiKey, httpClient, maxRetries);
        }

    }
}
