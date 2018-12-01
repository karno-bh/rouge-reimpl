package il.ac.sce.ir.metric.starter.gui.main.util;

import il.ac.sce.ir.metric.starter.gui.main.model.AppModel;

import java.util.ArrayList;
import java.util.List;

public class ModelsManager {

    private final List<AppModel> allModels = new ArrayList<>();

    public void register(AppModel appModel) {
        this.allModels.add(appModel);
    }

    public void publishAll() {
        allModels.forEach(AppModel::publishSelf);
    }
}
