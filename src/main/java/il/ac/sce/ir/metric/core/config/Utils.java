package il.ac.sce.ir.metric.core.config;

import java.util.Map;
import java.util.List;

public class Utils {

    public void printHelpAndExitProcess() {
        // TODO Apply an appropriate help
        System.out.println("Here should come an appropriate help usage!");
        System.exit(0);
    }

    public void printErrorMessageAndExitProcess(String message) {
        System.err.println(message);
        System.exit(-1);
    }

    public<T> T requireJSONTypeAndCast(Object object, String requiredProperty, Class<T> expectedObjClass) {

        String resolvedName;
        boolean wasCheckPassed = false;
        if (List.class.isAssignableFrom(expectedObjClass)) {
            resolvedName = "array";
            wasCheckPassed = List.class.isAssignableFrom(object.getClass());
        } else if (Map.class.isAssignableFrom(expectedObjClass)) {
            resolvedName = "object";
            wasCheckPassed = Map.class.isAssignableFrom(object.getClass());
        } else if (String.class.isAssignableFrom(expectedObjClass)) {
            resolvedName = "string";
            wasCheckPassed = String.class.isAssignableFrom(object.getClass());
        } else if (Boolean.class.isAssignableFrom(expectedObjClass)) {
            resolvedName = "boolean";
            wasCheckPassed = Boolean.class.isAssignableFrom(object.getClass());
        } else if (Number.class.isAssignableFrom(expectedObjClass)) {
            resolvedName = "number";
            wasCheckPassed = Number.class.isAssignableFrom(object.getClass());
        } else {
            throw new RuntimeException("Got unexpected class to be assigned");
        }

        if (!wasCheckPassed) {
            throw new RuntimeException("JSON Error: \"" + requiredProperty + "\" should be of " + resolvedName + " type");
        }
        return (T) object;
    }
}
