package View;

import Server.Server;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * this is the class Controller of the Properties Stage
 * @authors ben moalem & idan revah
 */

public class PropertiesController implements Initializable {
    private Stage stage;

    private String algorithmString;
    public ChoiceBox algorithmChoiceBox;

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void saveChanges(){
        algorithmString = (String)algorithmChoiceBox.getValue();

        //if the gamer chose something
        if(algorithmString != null) {
            if (algorithmString.equals("BreadthFirstSearch"))
                setAlgo("BreadthFirstSearch");
            else if (algorithmString.equals("BestFirstSearch"))
                setAlgo("BestFirstSearch");
            else if (algorithmString.equals("DepthFirstSearch"))
                setAlgo("DepthFirstSearch");

            //closing the stage
            stage.close();
        }
        else {//if he didnt chose..
            //show an alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You have to choose something to save");
            alert.showAndWait();
        }

        //Server.Configurations.setNewProperties(algorithmString, generatorString);

        //Server.Configurations.run();
    }


    public void closeButton(){
        stage.close();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmChoiceBox.getItems().addAll("BreadthFirstSearch","BestFirstSearch","DepthFirstSearch");
        //String searchValue = Server.Configurations.getValue("algorithms_solveAlgorithm");
        //algorithmChoiceBox.setValue(searchValue);
    }

    //setting the new algorithem
    public void setAlgo(String name){
        String workingDirectory =System.getProperty("user.dir");
        String propertyFileDirectory="\\Resources";
        FileOutputStream outToFile;
        Properties prop = new Properties();
        try {
            outToFile=new FileOutputStream(workingDirectory+propertyFileDirectory+"\\config.properties");
            prop.setProperty("searchingAlgorithm" , name);
            prop.store(outToFile,null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
