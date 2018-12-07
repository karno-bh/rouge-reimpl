package il.ac.sce.ir.metric.starter.gui.main.data;

import java.util.List;

public class MetricWrapper {

    private String metric;

    private List<String> subMetric;

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public List<String> getSubMetric() {
        return subMetric;
    }

    public void setSubMetric(List<String> subMetric) {
        this.subMetric = subMetric;
    }
}
