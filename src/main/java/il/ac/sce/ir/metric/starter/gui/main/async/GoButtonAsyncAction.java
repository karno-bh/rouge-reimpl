package il.ac.sce.ir.metric.starter.gui.main.async;

import il.ac.sce.ir.metric.core.container.Container;
import il.ac.sce.ir.metric.core.container.container_algorithm.MainAlgo;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.starter.gui.main.model.GoButtonModel;
import il.ac.sce.ir.metric.starter.gui.main.util.ModelsToConfigConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;

public class GoButtonAsyncAction implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GoButtonModel goButtonModel;


    public GoButtonAsyncAction(GoButtonModel goButtonModel) {
        this.goButtonModel = goButtonModel;
    }

    @Override
    public void run() {
        try {
            startMainLogic();
        } catch (Throwable t) {
            logger.error("Error while processing metrics", t);
        }
        SwingUtilities.invokeLater(() -> {
            // goButtonModel.setGoButtonEnabled(true);
            goButtonModel.setMetricsInRun(false);
            goButtonModel.checkEnablement();
        });
    }

    private void startMainLogic() {
        ModelsToConfigConverter configConverter = new ModelsToConfigConverter();
        configConverter.setMetricPanelModel(goButtonModel.getMetricPanelModel());
        configConverter.setRougeSelectionPanelModel(goButtonModel.getRougeSelectionPanelModel());
        configConverter.setReadabilitySelectionPanelModel(goButtonModel.getReadabilitySelectionPanelModel());
        configConverter.setAutoSummENGSelectionPanelModel(goButtonModel.getAutoSummENGSelectionPanelModel());

        Configuration configuration = configConverter.convert();

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

    /*public List<String> getSelectedMetrics(GoButtonModel goButtonModel) {

    }*/
}
