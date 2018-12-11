package View;

import Model.*;
import ViewModel.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        MyModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);

        primaryStage.setTitle("Maze Application");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
        Scene scene = new Scene(root, 630, 450);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        viewModel.addObserver(view);


        SetStageCloseEvent(primaryStage,model);
        primaryStage.show();
    }


    /**
     * this function ask the gamer if he sure that he wanna exit
     * @param primaryStage
     */
    private void SetStageCloseEvent(Stage primaryStage, MyModel model) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setContentText("Are you sure you want to exit?");
                ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("No, I wanna play more", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(yesButton,noButton);

                //checking what the player choose
                alert.showAndWait().ifPresent((buttonType) -> {
                    if(buttonType == yesButton){
                        model.stopServers();
                    } });
                windowEvent.consume();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}