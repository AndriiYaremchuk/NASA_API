import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import java.util.*;
import java.util.stream.Collectors;


public class Tests extends TestBase {
    Utils util = new Utils();

   public void verifyCuriosityPhotosOn1000Sol (){
       request.setRequestParams("?sol=1000");
       String solResponse = HTTPRequest.RequestService.sendHTTPRequest(request);

       //get sol JSONs
       JSONObject solJSONObj = new JSONObject(solResponse);
       JSONArray solResponseAsArray = solJSONObj.optJSONArray("photos");
       List<JSONObject> solResponseList = new ArrayList<>();
       for (int i = 0; i < solResponseAsArray.length(); i++) {
           solResponseList.add((JSONObject) solResponseAsArray.get(i));
       }
       //sort retrieved JSONs by id's
       Comparator<JSONObject> byId = Comparator.comparing(element -> element.getInt("id"));
       List<JSONObject> solSortedJson = solResponseList.stream().sorted(byId).collect(Collectors.toList());
       //get the first 10 sol photos
       List<String> solPhotosList = solSortedJson.stream()
               .map(el -> (String) JsonPath.parse(el.toString())
                       .read("$.img_src")).limit(10).collect(Collectors.toList());

       //retrieve earth date from sol response
       String earthDate = solSortedJson.stream().findFirst()
               .map(el -> (String) JsonPath.parse(el.toString())
                       .read("$.earth_date")).get();
       //get earth date JSONs
       request.setRequestParams("?earth_date=" + earthDate);
       String earthResponse = HTTPRequest.RequestService.sendHTTPRequest(request);

       JSONObject earthJSONObj = new JSONObject(earthResponse);
       JSONArray earthResponseAsArray = earthJSONObj.optJSONArray("photos");

       List<JSONObject> earthResponseList = new ArrayList<>();
       for (int i = 0; i < earthResponseAsArray.length(); i++) {
           earthResponseList.add((JSONObject) earthResponseAsArray.get(i));
       }

       List<JSONObject> earthSortedJson = solResponseList.stream().sorted(byId).collect(Collectors.toList());
       //get the first 10 earth date photos
       List<String> earthPhotosList = earthSortedJson.stream()
               .map(el -> (String) JsonPath.parse(el.toString())
                       .read("$.img_src")).limit(10).collect(Collectors.toList());
       //read photo's bytes
       ArrayList<byte[]> solPhotoList = new ArrayList<>();
       for (String solPhoto : solPhotosList) {
           solPhotoList.add(util.imageToByteArray(solPhoto));
       }

       ArrayList<byte[]> earthPhotoList = new ArrayList<>();
       for (String earthPhoto : earthPhotosList) {
           earthPhotoList.add(util.imageToByteArray(earthPhoto));
       }
       //Assert image equality
       for (int i = 0; i < solPhotoList.size(); i++) {
           Assert.assertTrue(Arrays.equals(solPhotoList.get(i), earthPhotoList.get(i)));
       }
   }

}

