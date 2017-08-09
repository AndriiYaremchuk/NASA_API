import org.json.JSONObject;

import java.util.Comparator;

public class ResponseComparator implements Comparator<JSONObject> {
    @Override
    public int compare(JSONObject o1, JSONObject o2) {
        Integer firstId = o1.getInt("id");
        Integer secondId = o2.getInt("id");
        return firstId.compareTo(secondId);
    }
}
