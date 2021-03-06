package il.ac.sce.ir.metric.starter.command_line.main.container.algo;

import il.ac.sce.ir.metric.concrete_metric.common.util.ParallelPreCache;
import il.ac.sce.ir.metric.core.sync.Arbiter;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.container.Container;
import il.ac.sce.ir.metric.core.container.container_algorithm.MainAlgo;
import il.ac.sce.ir.metric.core.reducer.Reducer;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.utils.file_system.CategoryPathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

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
        try {
            ParallelPreCache parallelPreCache = (ParallelPreCache)container.getBean(Constants.READABILITY_PRE_CACHE);
            parallelPreCache.preCache(container.getConfiguration());
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

            Arbiter arbiter = (Arbiter)container.getBean(Constants.ARBITER);
            arbiter.release();

            Object possibleReducer = container.getBean(Constants.COMBINE_REDUCER);
            if (possibleReducer != null && possibleReducer instanceof Reducer) {
                logger.info("Running reducers");
                Reducer reducer = (Reducer) possibleReducer;
                reducer.reduce();
            }
        } finally {
            ExecutorService mainThreadPool = (ExecutorService) container.getBean(Constants.MAIN_TRHEAD_POOL);
            mainThreadPool.shutdown();
        }

    }


    protected List<ProcessedCategory> resolveCategories(String startDirLocation) {
        CategoryPathResolver categoryPathResolver = new CategoryPathResolver();
        return categoryPathResolver.resolveCategories(startDirLocation);
    }

}

