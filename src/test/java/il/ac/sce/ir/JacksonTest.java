package il.ac.sce.ir;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JacksonTest {

    @Test
    public void simpleJackson() {
        try {
            Map<String, Object>  jsonData = new ObjectMapper().readValue(JacksonTest.class.getClassLoader().getResource("data_for_json.json"), LinkedHashMap.class);
            System.out.println(jsonData.get("models_path"));
            Object someArray = jsonData.get("some_array");
            System.out.println(someArray.getClass());
            for (Object o : (List) someArray) {
                System.out.println(o.getClass());
            }
            System.out.println(jsonData.get("inner_object").getClass());
            for (Map.Entry<String, Object> kv : jsonData.entrySet()) {
                System.out.println(kv.getKey());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
