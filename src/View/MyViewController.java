package View;


import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.generic.ExceptionThrower;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import static java.lang.Character.isDigit;

/**
 * this is the class Controller of the Main View
 * @authors ben moalem & idan revah
 */

public class MyViewController implements Observer, IView, Initializable {

    //Objects
    private MyViewModel viewModel;
    private GameController gameController;

    //images
    public ImageView img_player;
    public ImageView img_wall;
    public ImageView img_welcome;
    public ImageView img_choose_wall;
    public ImageView img_choose_player;
    public ImageView img_row;
    public ImageView img_col;
    public ImageView img_ready;

    //Buttons
    public Button btn_generateMaze;
    public Button btn_prev_wall;
    public Button btn_prev_player;
    public Button next_wall;
    public Button next_player;

    //Menu Items
    public MenuItem m_load;

    //text fields
    public TextField txt_rows;
    public TextField txt_columns;

    //Booleans for what to do
    private static Boolean generate = false;
    private static Boolean solve = false;
    private static Boolean move = false;
    private static Boolean done = false;
    private static Boolean loaded = false;

    //static int for indexing the images
    private static int indexOfPlayer = 1;
    private static int indexOfWall = 1;

    //media
    public static MediaPlayer mediaPlayer;
    public CheckBox check_box_mute;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }


    /**
     * this function update the VIEW with the given changes
     * @param o   is the Observable that changed
     * @param arg is what changed in the Observable
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {

            //if we changed the Maze[][] (generate was pressed)
            if (generate) {
                gameController.generate();
                generate = false;
            }
            //if we want to draw the Solution (solve was pressed)
            else if (solve) {
                gameController.solve();
                solve = false;
                done = true;
            }
            //if we want to draw the player again (movement was pressed)
            else if (move) {
                gameController.move();
            }
        }
    }


    //---------------------------//
    //--Setting the Game Images--//
    //---------------------------//

    //character image
    public void setCharacter() {
        gameController.setCharacter("resources/Pics/" + "player" + indexOfPlayer + ".jpg");
    }

    //wall image
    public void setWall() {
        gameController.setWall("resources/Pics/" + "wall" + indexOfWall + ".jpg");
    }


    //---------------------------------------//
    //those are the Displayers of the Objects//
    //--they are running the Draw function---//
    //-------------to the Canvas-------------//
    //--------------------------------------//
    @Override
    public void MazeDisplayer(Maze maze) {
        gameController.MazeDisplayer(maze);
    }

    @Override
    public void SolutionDisplayer(Solution solution, Maze maze) {
        gameController.SolutionDisplayer(solution, maze);
    }

    public void PlayerDisplayer(int characterPositionRow, int characterPositionColumn, Maze maze) {
        gameController.PlayerDisplayer(characterPositionRow, characterPositionColumn, maze);
    }


    //--------------------------//
    //-Button pressed functions-//
    //-------------------------//

    //if the Exit button pressed
    public void Exit(){
        viewModel.exit();
    }

    //if the mute button pressed
    public void mute(){
        mediaPlayer.setMute(true);
        if (!check_box_mute.isSelected())
            mediaPlayer.setMute(false);
    }

    //if the Generate Button Pressed
    public void MazeGenerator() {
        generate = true;
        done = false;
        int height, width;

        //check if he entered a NUMBER
        try
        {
            height = Integer.parseInt(txt_rows.getText());
            width = Integer.parseInt(txt_columns.getText());
            if(width < 6 || height < 6){
                throw new ArithmeticException();
            }


            viewModel.generateMaze(width, height);//send to viewModel
        }
        catch(Exception e)//if not throw an alert
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            if(e.getClass() == ArithmeticException.class)
                alert.setContentText("this maze is too small..");
            else
                alert.setContentText("You have to enter valid numbers");
            alert.showAndWait();

            //closing the game stage
            gameController.closeStage();
        }
    }

    //if the Solve Button Pressed
    public void MazeSolver() {
        //make sure the maze already exists
        if (viewModel.getMaze() != null) {
            solve = true;
            viewModel.solveMaze();//get Solution
        }
    }

    //if some key pressed
    public void KeyPressed(KeyEvent keyEvent) {
        if (!done) {
            move = true;
            viewModel.moveCharacter(keyEvent.getCode());
        }
        keyEvent.consume();
    }


    //----------------------------//
    //-----opening new Scene-----//
    //--------------------------//

    //if the generate Button pressed
    //opening new Stage to show in
    public void GameScene(ActionEvent actionEvent) {
        try {
            //openning new Stage to show in
            Stage stage = new Stage();
            stage.setTitle("Idan & Ben Maze");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Game.fxml").openStream());
            Scene scene = new Scene(root, 600, 650);
            stage.setScene(scene);

            //loading the controllers of the new stage:
            GameController game = fxmlLoader.getController();
            setGameController(game);
            game.setAll(stage, viewModel, this);

            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (loaded){
            gameController.generate();
            done = false;
            loaded = false;
        }
        else {
            MazeGenerator();
        }
    }

    //if the properties Button pressed
    //opening new Stage to show in
    //if the properties Button pressed
    //opening new Stage to show in
    public void propScene(ActionEvent actionEvent) {
        try {
            //openning new Stage to show in
            Stage stage = new Stage();
            stage.setTitle("Properties");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Properties.fxml").openStream());
            Scene scene = new Scene(root, 400, 120);
            stage.setScene(scene);
            stage.setResizable(false);
            //loading the controllers of the new stage:
            PropertiesController properties = fxmlLoader.getController();
            properties.setStage(stage);

            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //if the about Button pressed
    //opening new Stage to show in
    public void AboutScene(ActionEvent actionEvent) {
        OpenNewScene("About the programmers", "About.fxml", 500, 400);
    }

    //if the help Button pressed
    //opening new Stage to show in
    public void HelpScene(ActionEvent actionEvent) {
        OpenNewScene("Help in game", "Help.fxml", 450, 425);
    }

    // opening load scene
    public void loadScene (ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a maze to load");
        fileChooser.setInitialDirectory(new File(".\\Resources/Mazes"));
        File file = fileChooser.showOpenDialog(new PopupWindow() {
        });
        if(file != null && file.exists() && !file.isDirectory()){
            viewModel.loadFile(file);
            loaded = true;
            done = false;
            GameScene(event);

        }
        event.consume();

    }

    //this function opens New Scene with given fxml File
    private void OpenNewScene(String title, String fxml_File, int width, int height) {
        try {
            //openning new Stage to show in
            Stage stage = new Stage();
            stage.setTitle(title);
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource(fxml_File).openStream());
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //-----------------//
    //----Initialize---//
    //---When opening--//
    //-----------------//

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // init player
        setImage(img_player,"Pics/player1.jpg");
        //init wall
        setImage(img_wall,"Pics/wall1.jpg");
        // init welcome
        setImage(img_welcome,"Pics/welcome.jpg");
        // init choose player photo
        setImage(img_choose_player,"Pics/choose_player.jpg");
        // init choose wall photo
        setImage(img_choose_wall,"Pics/choose_wall.jpg");
        //init row photo
        setImage(img_row,"Pics/Rows.jpg");
        //init col photo
        setImage(img_col,"Pics/Columns.jpg");
        //init ready photo
        setImage(img_ready,"Pics/ready.jpg");
        // set music on
        music();
    }

    // this function is for playing music
    private void music(){
        //String bip = ".\\Resources/music1.mp3";
        Media hit = new Media(this.getClass().getClassLoader().getResource("music1.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(hit);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setStartTime(Duration.seconds(0));
        mediaPlayer.setStopTime(Duration.seconds(21));
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }


    //--------------------//
    //-----Set images-----//
    //--------------------//

    public void nextPlayer() {
        indexOfPlayer = (indexOfPlayer + 1) % 4;
        setImage(img_player,"Pics/player" + Math.abs(indexOfPlayer) + ".jpg");
    }
    public void prevPlayer() {
        indexOfPlayer = (indexOfPlayer - 1) % 4;
        setImage(img_player,"Pics/player" + Math.abs(indexOfPlayer) + ".jpg");
    }
    public void nextWall() {
        indexOfWall = (indexOfWall + 1) % 4;
        setImage(img_wall,"Pics/wall" + Math.abs(indexOfWall) + ".jpg");
    }
    public void prevWall() {
        indexOfWall = (indexOfWall - 1) % 4;
        setImage(img_wall,"Pics/wall" + Math.abs(indexOfWall) + ".jpg");
    }
    public void setImage(ImageView imageView, String filePath) {
        //File file = new File(filePath);
        Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(filePath));
        imageView.setImage(image);
    }
}

