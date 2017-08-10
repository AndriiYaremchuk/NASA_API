import com.jayway.jsonpath.JsonPath;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import java.util.*;
import java.util.stream.Collectors;


public class Tests extends TestBase {
    Utils util = new Utils();
    Logger logger= Logger.getLogger(Tests.class);

   public void verifyCuriosityPhotosOn1000Sol (){
       request.setRequestParams("?sol=1000");
       String solResponse = HTTPRequest.RequestService.sendHTTPRequest(request);
       logger.info("Response retrieved"+solResponse);

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
       logger.info("Earth Date: "+earthDate);

       //get earth date JSONs
       request.setRequestParams("?earth_date=" + earthDate);
       String earthResponse = HTTPRequest.RequestService.sendHTTPRequest(request);

       List<JSONObject> earthResponseList = util.getJSONListFromResponse(earthResponse,"photos");
       List<JSONObject> earthSortedJson = util.sortJSON(earthResponseList);

       //get the first 10 earth date photos
       List<String> earthPhotosList = earthSortedJson.stream()
               .map(el -> (String) JsonPath.parse(el.toString())
                       .read("$.img_src")).limit(10).collect(Collectors.toList());
        logger.info("Reading photos bytes...");

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

   public void verifyPhotosTakenByEachCameraSol1000(){
       request.setRequestParams("?sol=1000");
       String solResponse = HTTPRequest.RequestService.sendHTTPRequest(request);

       // get camera names for further requests
       List<String> camerasList = util.getStringListFromJSONArray(solResponse,"photos",
               "rover", "cameras", "name");

       Map<String, Integer> shotsTakenMap = new HashMap<>();
       for(String camera : camerasList){
           request.setRequestParams("?sol=1000&camera="+camera);
           String response = HTTPRequest.RequestService.sendHTTPRequest(request);
           JSONObject JSONObj = new JSONObject(response);
           JSONArray responseAsArray = JSONObj.optJSONArray("photos");
           shotsTakenMap.put(camera, responseAsArray.length());
       }

       Assert.assertTrue(util.
               compareCountOfPhotos(
                       shotsTakenMap.values().stream()
                               .map(element -> element.intValue()).collect(Collectors.toList())));

   }

}

