package il.ac.sce.ir.metric.core.reporter.utils;

import il.ac.sce.ir.metric.core.config.Constants;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CommonFileReporter {

    public String buildHeader(Set<String> keys, String... additionalColumnsFromLeft) {
        StringBuilder headerBuf = new StringBuilder(256);

        for (String additionalLeftColumn : additionalColumnsFromLeft) {
            headerBuf.append(additionalLeftColumn).append(Constants.CSV_REPORT_SEPARATOR);
        }

        boolean first = true;
        for (String key : keys) {
            if (!first) {
                headerBuf.append(Constants.CSV_REPORT_SEPARATOR);
            } else {
                first = false;
            }
            headerBuf.append(key);
        }
        return headerBuf.toString();
    }

    public void convertMetricsToReportLine(Map<String, Object> properties,
                                            Collection<String> keys,
                                            StringBuilder reportLineBuf) {
        for (String key : keys) {
            Number scoreValue = (Number)properties.get(key);
            reportLineBuf.append(Constants.CSV_REPORT_SEPARATOR).append(scoreValue.doubleValue());
        }
    }
}
