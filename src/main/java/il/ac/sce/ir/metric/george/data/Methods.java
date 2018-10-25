package il.ac.sce.ir.metric.george.data;

import il.ac.sce.ir.metric.core.config.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Methods {

    public static final String N_GRAMS = Constants.GEORGE_METHOD_N_GRAMS;
    public static final String WORD_GRAPHS = Constants.GEORGE_METHOD_WORD_GRAPHS;
    public static final String PLACEHOLDER = Constants.GEORGE_METHOD_PLACEHOLDER;
    public static final String PLACEHOLDER_SS = Constants.GEORGE_METHOD_PLACEHOLDER_SS;
    public static final String RANDOM = Constants.GEORGE_METHOD_RANDOM;
    public static final String COSINE = Constants.GEORGE_METHOD_COSINE;
    public static final String PLACE_HOLDER_EXTRAWEIGHT = Constants.GEORGE_METHOD_PLACE_HOLDER_EXTRAWEIGHT;


    private final Map<String, Boolean> requiredMethods = new HashMap<>();

    public void setMethod(String method, boolean enabled) {
        if (method == null || method.isEmpty()) {
            throw new RuntimeException("Required method cannot be null");
        }
        requiredMethods.put(method, enabled);
    }

    public void addMethod(String method) {
        this.setMethod(method, true);
    }

    public boolean isEnabled(String method) {
        Boolean requiredMethodEnablement = requiredMethods.get(method);
        if (requiredMethodEnablement == null) {
            return false;
        }
        return requiredMethodEnablement;
    }

    public static Methods all() {
        Methods methods = new Methods();
        methods.addMethod(N_GRAMS);
        methods.addMethod(WORD_GRAPHS);
        methods.addMethod(PLACEHOLDER);
        methods.addMethod(PLACEHOLDER_SS);
        methods.addMethod(RANDOM);
        methods.addMethod(COSINE);
        methods.addMethod(PLACE_HOLDER_EXTRAWEIGHT);

        return methods;
    }
}
