package il.ac.sce.ir.metric.core.utils.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.sce.ir.metric.core.score.ReportedProperties;

import java.util.HashMap;
import java.util.Map;

public class ObjectToMapConverter {

    public Map<String, Object> getReportedProperties(ReportedProperties reportedProperties) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> properties = objectMapper.convertValue(reportedProperties, Map.class);
        Map<String, Object> filteredProperties = new HashMap<>();
        for (Map.Entry<String, Object> prop : properties.entrySet()) {
            String propertyName = prop.getKey();
            if (reportedProperties.resolveReportedProperties().contains(propertyName)) {
                filteredProperties.put(propertyName, prop.getValue());
            }
        }
        return filteredProperties;
    }
}
