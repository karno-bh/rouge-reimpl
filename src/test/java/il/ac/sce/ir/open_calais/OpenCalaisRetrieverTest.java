package il.ac.sce.ir.open_calais;

import entity_extractor.TextEntities;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.FileSystemCacheTextProcessor;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.george.open_calais.processor.OpenCalaisEntiryResolverTextProcessor;
import il.ac.sce.ir.metric.george.open_calais.processor.OpenCalaisRetrieverTextProcessor;
import org.apache.commons.httpclient.HttpClient;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

public class OpenCalaisRetrieverTest {

    private static final String DEFAULT_URL = "https://api.thomsonreuters.com/permid/calais";

    private static final int DEFAULT_SLEEP_TIME_MS = 500;

    private static final int DEFAULT_MAX_RETRIES = 10;

    private static final String[] DEFAULT_HTTP_CLIENT_USER_AGENT = {"http.useragent", "Calais Rest Client"};

    private static final String API_KEY = "iSDDWuAd3qriwV0UYpkepAXjgamaI8BE";

    private static final String fileSystemCachePrefix = "c:/my/temp/cache";

    private static final String fsCacheCategory = "open-calais-responcsee";

    @Test
    public void openCalaisRetrieveTest() {
        final String fileName = "c:/my/temp/sample.txt";

//        File file = new File(fileName);
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter(DEFAULT_HTTP_CLIENT_USER_AGENT[0], DEFAULT_HTTP_CLIENT_USER_AGENT[1]);
        OpenCalaisRetrieverTextProcessor openCalaisRetrieverTextProcessor =
                new OpenCalaisRetrieverTextProcessor(DEFAULT_URL,
                        DEFAULT_SLEEP_TIME_MS, API_KEY, httpClient, DEFAULT_MAX_RETRIES);


        TextPipelineExtractor<String, String> openCalaisResultExtractor = new TextPipelineExtractor<>();
        new TextPipeline<>(openCalaisRetrieverTextProcessor)
                .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fileSystemCachePrefix, fsCacheCategory, new ConcurrentHashMap<>()))
                .extract(openCalaisResultExtractor);
        TextProcessor<String, String> textProcessor = openCalaisResultExtractor.getTextProcessor();

        Text<String> result = textProcessor.process(Text.asFileLocation(fileName));

        System.out.println("Got result from Open Calais: \n" + result.getTextData());
    }


    @Test
    public void openCalaisEntiryResolverTest() {
        final String fileName = "c:/my/temp/sample.txt";

//        File file = new File(fileName);
        HttpClient httpClient = new HttpClient();
        httpClient.getParams().setParameter(DEFAULT_HTTP_CLIENT_USER_AGENT[0], DEFAULT_HTTP_CLIENT_USER_AGENT[1]);
        OpenCalaisRetrieverTextProcessor openCalaisRetrieverTextProcessor =
                new OpenCalaisRetrieverTextProcessor(DEFAULT_URL,
                        DEFAULT_SLEEP_TIME_MS, API_KEY, httpClient, DEFAULT_MAX_RETRIES);


        TextPipelineExtractor<String, TextEntities> openCalaisEntityResolver = new TextPipelineExtractor<>();
        new TextPipeline<>(openCalaisRetrieverTextProcessor)
                .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fileSystemCachePrefix, fsCacheCategory, new ConcurrentHashMap<>()))
                .pipe(new OpenCalaisEntiryResolverTextProcessor())
                .extract(openCalaisEntityResolver);

        TextProcessor<String, TextEntities> textProcessor = openCalaisEntityResolver.getTextProcessor();

        Text<TextEntities> result = textProcessor.process(Text.asFileLocation(fileName));
        System.out.println("from: " + result.getTextId() + " got extracted those entities: \n" + result.getTextData().getText());

        result.getTextData().getEntities().forEach(System.out::println);


    }
}
