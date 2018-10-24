package il.ac.sce.ir.metric.george.open_calais.processor;

import entity_extractor.ExtractedEntity;
import entity_extractor.TextEntities;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class OpenCalaisEntiryResolverTextProcessor implements TextProcessor<String, TextEntities> {

    @Override
    public Text<TextEntities> process(Text<String> data) {

        TextEntities entities = new TextEntities();

        String responseStr = data.getTextData();
        JSONObject obj = new JSONObject(responseStr);

        // Get the document text from the response
        String text = obj.getJSONObject("doc").getJSONObject("info").getString("document");
        entities.setText(text);

        {
            // Create blacklist with names of entities to ignore
            ArrayList<String> blacklist = new ArrayList<>();
            blacklist.add("DEV");
            blacklist.add("http");
            blacklist.add("html");

            // Get entities
            Iterator<String> tags = obj.keys();
            while (tags.hasNext()) {
                String tagID = tags.next();

                if (tagID.equals("doc"))
                    continue;

                JSONObject tag = obj.getJSONObject(tagID);

                // Check that object has at least the basic properties that we need to continue
                if (!tag.has("_typeGroup") || !tag.has("name") || !tag.has("_type") || !tag.has("instances")) {
                    continue;
                }

                // If object is not of type "entities", ignore it as we only want entities
                String typeGroup = tag.getString("_typeGroup");
                if (!typeGroup.equals("entities"))
                    continue;

                // Skip tags that are not for end user display
//                if (tag.getString("forenduserdisplay").equals("false")) {
//                    continue;
//                }

                // Get all of this entity's instances
                JSONArray instances = tag.getJSONArray("instances");

                for (Object instance : instances) {
                    // Check that the object is indeed a JSONObject before casting it
                    if (!(instance instanceof JSONObject)) {
                        // LOGGER.log(Level.SEVERE, "Instance is not a JSONObject! Skipping it...");
                        continue;
                    }

                    // Cast instance to a JSONObject to continue
                    JSONObject i = (JSONObject) instance;

                    if (!i.has("offset") || !i.has("length")) {
//                        LOGGER.log(Level.SEVERE, "Instance does not have offset or length! Skipping it...");
                        continue;
                    }

                    // Initialize entity for this instance and add the required properties to it
                    ExtractedEntity entity = new ExtractedEntity();

//                    entity.setName(i.getString("exact"));
                    entity.setName(tag.getString("name"));
                    entity.setType(tag.getString("_type"));
                    entity.setOffset(i.getInt("offset"));
                    entity.setLength(i.getInt("length"));

                    // Only add entity if its name is not blacklisted
                    if (!blacklist.contains(entity.getName())) {
                        entities.addEntity(entity);
                    }
                }
            }
        }

        return new Text<>(data.getTextId(), entities);
    }
}
