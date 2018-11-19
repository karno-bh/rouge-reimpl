package il.ac.sce.ir.metric.core.reporter.utils;

import il.ac.sce.ir.metric.core.config.Constants;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CommonFileReporter {

    public String buildHeader(Set<String> sortedKeys) {
        StringBuilder headerBuf = new StringBuilder(256);
        headerBuf.append(Constants.PEER);

        for (String key : sortedKeys) {
            headerBuf.append(Constants.CSV_REPORT_SEPARATOR).append(key);
        }
        return headerBuf.toString();
    }

    public void convertMetricsToReportLine(Map<String, Object> properties,
                                            Collection<String> sortedKeys,
                                            StringBuilder reportLineBuf) {
        for (String key : sortedKeys) {
            Number scoreValue = (Number)properties.get(key);
            reportLineBuf.append(Constants.CSV_REPORT_SEPARATOR).append(scoreValue.doubleValue());
        }
    }
}
