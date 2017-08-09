import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public byte[] imageToByteArray (String desiredUrl){
        InputStream is;
        URL url = null;
        try {
            url = new URL(desiredUrl);
            is = url.openStream();
            byte[] imageBytes = IOUtils.toByteArray(is);
            is.close();
            return imageBytes;
        } catch (IOException e) {
            System.err.printf("Failed while reading bytes from %s: %s",
                    url.toExternalForm(), e.getMessage());
            return null;
        }
    }
    public List<JSONObject> sortJSON (List<JSONObject> input) {
       Comparator<JSONObject> byId = Comparator.comparing(element -> element.getInt("id"));
       return input.stream().sorted(byId).collect(Collectors.toList());
    }

    public List<JSONObject> getJSONListFromResponse (String response, String key) {
        JSONObject JSONObj = new JSONObject(response);
        JSONArray ResponseAsArray = JSONObj.optJSONArray(key);
        List<JSONObject> responseList = new ArrayList<>();
        for (int i = 0; i < ResponseAsArray.length(); i++) {
            responseList.add((JSONObject) ResponseAsArray.get(i));
        }
        return responseList;
    }
}
