package il.ac.sce.ir.open_calais;

import entity_extractor.TextEntities;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.config.Configuration;
import il.ac.sce.ir.metric.core.config.ProcessedCategory;
import il.ac.sce.ir.metric.core.processor.FileSystemCacheTextProcessor;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.utils.CategoryPathResolver;
import il.ac.sce.ir.metric.george.data.Methods;
import il.ac.sce.ir.metric.george.open_calais.processor.OpenCalaisEntiryResolverTextProcessor;
import il.ac.sce.ir.metric.george.open_calais.processor.OpenCalaisRetrieverTextProcessor;
import il.ac.sce.ir.metric.george.reporter.GeorgeReporter;
import org.apache.commons.httpclient.HttpClient;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GeorgeMetricReporterTest {

    private static final String DEFAULT_URL = "https://api.thomsonreuters.com/permid/calais";

    private static final int DEFAULT_SLEEP_TIME_MS = 500;

    private static final int DEFAULT_MAX_RETRIES = 10;

    private static final String[] DEFAULT_HTTP_CLIENT_USER_AGENT = {"http.useragent", "Calais Rest Client"};

    private static final String API_KEY = "iSDDWuAd3qriwV0UYpkepAXjgamaI8BE";

    private static final String fileSystemCachePrefix = "c:/my/temp/cache";

    private static final String fsCacheCategory = "open-calais-responcsee";

    @Test
    public void goegeMetricReporterTest() {

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


        Configuration configuration = Configuration.as()
                .workingSetDirectory("c:\\my\\learning\\final-project\\git\\temp-corpus-for-processing\\attempt001")
                .build();

        Methods methods = Methods.all();
        CategoryPathResolver categoryPathResolver = new CategoryPathResolver();
        List<ProcessedCategory> processedCategories = categoryPathResolver.resolveCategories(configuration.getWorkingSetDirectory());

        GeorgeReporter georgeReporter = new GeorgeReporter();
        georgeReporter.setConfiguration(configuration);
        georgeReporter.setMethods(methods);
        georgeReporter.setEntitiesTextProcessor(openCalaisEntityResolver.getTextProcessor());

        processedCategories.forEach((processedCategory -> georgeReporter.report(processedCategory, "all")));

    }
}
