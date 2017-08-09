import org.testng.annotations.Test;

public class App {
    Tests tests = new Tests();
    @Test
    public void testPhotos (){
        tests.verifyCuriosityPhotosOn1000Sol();
    }
}
