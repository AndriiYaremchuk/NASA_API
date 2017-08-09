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
       List<JSONObject> solResponseList = util.getJSONListFromResponse(solResponse,"photos");

       //sort retrieved JSONs by id's
       List<JSONObject> solSortedJson = util.sortJSON(solResponseList);

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

       List<JSONObject> earthResponseList = util.getJSONListFromResponse(earthResponse,"photos");
       List<JSONObject> earthSortedJson = util.sortJSON(earthResponseList);

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

