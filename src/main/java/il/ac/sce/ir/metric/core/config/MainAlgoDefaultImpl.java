package il.ac.sce.ir.metric.core.config;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import il.ac.sce.ir.metric.core.reducer.CombineReducer;
import il.ac.sce.ir.metric.core.reducer.Reducer;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class MainAlgoDefaultImpl implements MainAlgo {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private AtomicReference<Container> container;

    @Override
    public void setContainer(Container container) {
        this.container = new AtomicReference<>(container);
    }

    @Override
    public Container getContainer() {
        return container.get();
    }

    @Override
    public void run() {

        Container container = getContainer();
        String startDirLocation = container.getConfiguration().getWorkingSetDirectory();
        List<ProcessedCategory> categories = resolveCategories(startDirLocation);

        for (ProcessedCategory category : categories) {
            for (String requiredMetric : container.getConfiguration().getRequiredMetrics()) {
                String beanKey = requiredMetric.trim().toLowerCase();
                Object bean = container.getBean(beanKey);
                if (bean != null) {
                    logger.info("Processing category: {}, metric {}", category.getDescription(), requiredMetric);
                    Reporter reporter = (Reporter) bean;
                    reporter.report(category, requiredMetric);
                }
            }
        }

        Object possibleReducer = container.getBean(Constants.COMBINE_REDUCER);
        if (possibleReducer != null && possibleReducer instanceof Reducer) {
            logger.info("Running reducers");
            Reducer reducer = (Reducer) possibleReducer;
            reducer.reduce();
        }

    }

    protected List<ProcessedCategory> resolveCategories(String startDirLocation) {
        File startDir = new File(startDirLocation);
        String[] categoriesDirs = startDir.list((startDirInnerFile, fileName) -> startDirInnerFile.isDirectory());
        if (categoriesDirs == null || categoriesDirs.length == 0) {
            throw new RuntimeException("Nothing to process. No category found at " + startDirLocation);
        }
        return Arrays.stream(categoriesDirs)
                .map(category -> {
                    File categoryDescriptionFile = new File(category + File.separator + Constants.DESCRIPTION_FILE);
                    String description = null;
                    if (categoryDescriptionFile.isFile()) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(categoryDescriptionFile), StandardCharsets.UTF_8.name()))){
                            description = reader.readLine();
                        } catch (IOException ignored) {
                        }
                    }
                    return ProcessedCategory.as().dirLocation(category).description(description).build();
                })
                .collect(Collectors.toList());
    }

}
