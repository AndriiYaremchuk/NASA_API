import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequest {

    private String serviceUrl;
    private String requestParams;
    private static final String API_KEY = "&api_key=DEMO_KEY";

    public HTTPRequest(String requestParams) {
        setServiceUrl("https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos");
        setRequestParams(requestParams);
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    static class RequestService {

        public static String sendHTTPRequest (HTTPRequest httpRequest) {
            try {
                URL url = new URL(httpRequest.getServiceUrl() + httpRequest.getRequestParams() + API_KEY);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setUseCaches(false);
                connection.setDoInput(true);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } catch (Exception e) {
                throw new RuntimeException("Exception while calling service: " + e);
            }
        }
    }
}