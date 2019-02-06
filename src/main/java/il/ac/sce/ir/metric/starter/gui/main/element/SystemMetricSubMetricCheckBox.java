package il.ac.sce.ir.metric.starter.gui.main.element;

import javax.swing.*;

public class SystemMetricSubMetricCheckBox extends JCheckBox {

    private String system;

    private String metric;

    private String subMetric;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getSubMetric() {
        return subMetric;
    }

    public void setSubMetric(String subMetric) {
        this.subMetric = subMetric;
    }
}
