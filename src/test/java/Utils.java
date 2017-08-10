import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
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
    Logger logger = Logger.getLogger(Utils.class);

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
            logger.info("Failed while reading bytes from %s: %s"
                    + url.toExternalForm() + e.getMessage());
            return null;
        }
    }
    public List<JSONObject> sortJSON (List<JSONObject> input) {
       Comparator<JSONObject> byId = Comparator.comparing(element -> element.getInt("id"));
       return input.stream().sorted(byId).collect(Collectors.toList());
    }

    public List<JSONObject> getJSONListFromResponse (String response, String key) {
        JSONObject JSONObj = new JSONObject(response);
        JSONArray responseAsArray = JSONObj.optJSONArray(key);
        List<JSONObject> responseList = new ArrayList<>();
        for (int i = 0; i < responseAsArray.length(); i++) {
            responseList.add((JSONObject) responseAsArray.get(i));
        }
        return responseList;
    }

    public List<String> getStringListFromJSONArray (String response, String parseJSONkey, String JSONkey, String valueKey, String targetValue) {
        List<JSONObject> solResponseList = getJSONListFromResponse(response,parseJSONkey);
        List<String> result = new ArrayList<>();
        JSONArray names = solResponseList.get(0).getJSONObject(JSONkey).getJSONArray(valueKey);
        for(int i=0; i<names.length(); i++){
            List <JSONObject> objects = new ArrayList<>();
            objects.add((JSONObject) names.get(i));
            for (JSONObject ob : objects){
                result.add(ob.getString(targetValue));
            }
        }
        return result;
    }

    public boolean compareCountOfPhotos (List<Integer> input) {
        boolean result = true;
        for(int i=0; i<input.size(); i++){
            for(int j=1; j<input.size()-1;j++){
                try{if((input.get(i)%input.get(j))>=10) {
                    result = false;
                    }
                }catch (ArithmeticException e){
                    logger.info("Division by zero. One of cameras didn't take any photo");
                    result = false;
                }
            }
        }
        return result;
    }
}
