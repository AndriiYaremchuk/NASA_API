import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
}
