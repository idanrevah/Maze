package View;

import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * this is the class Controller of the About Scene
 * @authors  idan revah & ben moalem
 */

public class AboutController implements Initializable {

    public ImageView ben_moalem;
    public ImageView idan_revah;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File file = new File("Resources/Pics/ben.jpg");
        Image image = new Image(file.toURI().toString());
        ben_moalem.setImage(image);

        File file1 = new File("Resources/Pics/idan.jpg");
        Image image1 = new Image(file1.toURI().toString());
        idan_revah.setImage(image1);
    }
}
