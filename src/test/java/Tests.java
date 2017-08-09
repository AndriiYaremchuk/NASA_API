import com.jayway.jsonpath.JsonPath;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tests extends TestBase {
   public void verifyCuriosityPhotosOn1000Sol (){
       request.setRequestParams("?sol=1000");
       String solResponse = HTTPRequest.RequestService.sendHTTPRequest(request);

       JSONObject solJSONObj = new JSONObject(solResponse);
       JSONArray solResponseAsArray =solJSONObj.optJSONArray("photos");

       List<JSONObject> solResponseList = new ArrayList<>();
       for (int i=0; i< solResponseAsArray.length(); i++){
           solResponseList.add((JSONObject) solResponseAsArray.get(i));
       }

       Comparator<JSONObject> byId = Comparator.comparing(element-> element.getInt("id"));
       List<JSONObject> solSortedJson = solResponseList.stream().sorted(byId).collect(Collectors.toList());

       String earthDate = solSortedJson.stream().findFirst()
               .map(el ->(String) JsonPath.parse(el.toString())
                       .read("$.earth_date")).get();

       List<String> solPhotosList = solSortedJson.stream()
               .map(el ->(String) JsonPath.parse(el.toString())
                       .read("$.img_src")).limit(10).collect(Collectors.toList());


       request.setRequestParams("?earth_date="+earthDate);
       String earthResponse = HTTPRequest.RequestService.sendHTTPRequest(request);
       JSONObject earthJSONObj = new JSONObject(earthResponse);
       JSONArray earthResponseAsArray = earthJSONObj.optJSONArray("photos");
       List<JSONObject> earthResponseList = new ArrayList<>();
       for (int i=0; i< earthResponseAsArray.length(); i++){
           earthResponseList.add((JSONObject) earthResponseAsArray.get(i));
       }

       List<JSONObject> earthSortedJson = solResponseList.stream().sorted(byId).collect(Collectors.toList());

       List<String> earthPhotosList = earthSortedJson.stream()
               .map(el ->(String) JsonPath.parse(el.toString())
                       .read("$.img_src")).limit(10).collect(Collectors.toList());

       ArrayList<byte[]> solPhotoList = new ArrayList<>();
       for (String solPhoto : solPhotosList) {
           InputStream is;
           URL url = null;
           try {
               url = new URL(solPhoto);
               is = url.openStream();
               byte[] imageBytes = IOUtils.toByteArray(is);
               solPhotoList.add(imageBytes);
               is.close();
           } catch (IOException e) {
               System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());

           }
       }

       ArrayList<byte[]> earthlPhotoList = new ArrayList<>();
       for (String earthPhoto : earthPhotosList) {
           InputStream is;
           URL url = null;
           try {
               url = new URL(earthPhoto);
               is = url.openStream();
               byte[] imageBytes = IOUtils.toByteArray(is);
               earthlPhotoList.add(imageBytes);
               is.close();
           } catch (IOException e) {
               System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());

           }
       }

       for(int i=0; i<solPhotoList.size();i++){
           Assert.assertTrue(Arrays.equals(solPhotoList.get(i),earthlPhotoList.get(i)));
       }
       }

}

