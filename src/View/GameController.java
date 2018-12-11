package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;

import java.io.File;

/**
 * this is the class Controller of the Main View
 * @authors ben moalem & idan revah
 */

public class GameController implements IView{

    //Objects
    private Stage stage;
    private MyViewModel viewModel;
    private MyViewController viewController;
    public MazeDisplayer mazeDisplayer;
    public SolutionDisplayer solutionDisplayer;
    public PlayerDisplayer playerDisplayer;


    //positions
    private int characterPositionRow;
    private int characterPositionColumn;

    public Button btn_solveMaze;
    public Button btn_save;

    //check boxes
    public CheckBox check_box_mute;

    public void setAll(Stage stage, MyViewModel viewModel, MyViewController viewController) {
        this.stage = stage;
        this.viewModel = viewModel;
        this.viewController = viewController;
    }

    //---------------------------------------//
    //those are the Displayers of the Objects//
    //--they are running the Draw function---//
    //-------------to the Canvas-------------//
    //--------------------------------------//
    @Override
    public void MazeDisplayer(Maze maze) {
        mazeDisplayer.setMaze(maze);
    }
    @Override
    public void SolutionDisplayer(Solution solution, Maze maze) {
        solutionDisplayer.setSolution(solution,maze);
    }
    public void PlayerDisplayer(int characterPositionRow, int characterPositionColumn, Maze maze) {
        playerDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn, maze);
    }


    //---------------------//
    //---Drawing options---//
    //--------------------//

    //if we changed the Maze[][] (generate was pressed)
    public void generate() {
        MazeDisplayer(viewModel.getMaze());//maze
        solutionDisplayer.solutionClearRect();//clear old solution
        characterPositionRow = viewModel.getCharacterPositionRow();
        characterPositionColumn = viewModel.getCharacterPositionColumn();
        PlayerDisplayer(characterPositionRow,characterPositionColumn,viewModel.getMaze());//char position

        //setting player and wall
        viewController.setCharacter();
        viewController.setWall();
    }

    //if we want to draw the Solution (solve was pressed)
    public void solve() {
        SolutionDisplayer(viewModel.getSolution(), viewModel.getMaze());
    }

    //if we want to draw the player again (movement was pressed)
    public void move() {
        characterPositionRow = viewModel.getCharacterPositionRow();
        characterPositionColumn = viewModel.getCharacterPositionColumn();
        //display the player in the Canvas
        PlayerDisplayer(characterPositionRow,characterPositionColumn,viewModel.getMaze());

        //if the player got to the end point:
        if(viewModel.getMaze().isEndingPoint(characterPositionRow,characterPositionColumn)){
            //ask him for another game:
            Alert alert = new Alert(Alert.AlertType.NONE);
            //set victory music
            victoryMusic();
            alert.setTitle("Congratulations!");
            alert.setContentText("Wow can't believe you did it!");
            ButtonType yesButton = new ButtonType("Give me another Maze!", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("Maybe later", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(yesButton,noButton);

            //checking what the player choose
            alert.showAndWait().ifPresent((buttonType) -> {
                if(buttonType == yesButton){//generate another Maze
                    viewController.MazeGenerator();
                }
                else {
                    closeStage();
                }
            });

        }
    }


    //--------------------------//
    //-Button pressed functions-//
    //-------------------------//

    //if the mute button pressed
    public void mute(){
        viewController.mediaPlayer.setMute(true);
        if (!check_box_mute.isSelected())
            viewController.mediaPlayer.setMute(false);
    }

    //if the save maze button pressed
    public void saveMaze(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a directory to save the maze in");
        fileChooser.setInitialDirectory(new File(".\\Resources/Mazes"));

        //Show save file dialog
        File file = fileChooser.showSaveDialog(new PopupWindow() {
        });

        if(file != null) {
            viewModel.saveCurrentMaze(file);
            btn_save.setDisable(true);
        }

        event.consume();
    }

    //on MOUSE SCROLLING
    public void MouseScrolling(ScrollEvent scrollEvent) {
        if(scrollEvent.isControlDown()) {
            double zoomFactor = 1.05;
            double deltaY = scrollEvent.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }
            mazeDisplayer.setScaleX(mazeDisplayer.getScaleX() * zoomFactor);
            mazeDisplayer.setScaleY(mazeDisplayer.getScaleY() * zoomFactor);

            solutionDisplayer.setScaleX(solutionDisplayer.getScaleX() * zoomFactor);
            solutionDisplayer.setScaleY(solutionDisplayer.getScaleY() * zoomFactor);

            playerDisplayer.setScaleX(playerDisplayer.getScaleX() * zoomFactor);
            playerDisplayer.setScaleY(playerDisplayer.getScaleY() * zoomFactor);
        }
    }
    //if some key pressed
    public void KeyPressed(KeyEvent keyEvent) {
        viewController.KeyPressed(keyEvent);
    }

    //if the solve button pressed
    public void MazeSolver() {
        viewController.MazeSolver();
        btn_solveMaze.setDisable(true);
    }





    //--------------------//
    //-----Set images-----//
    //--------------------//

    //character image
    public void setCharacter(String characterPath){
        playerDisplayer.setImageFileNameCharacter(characterPath);
        if(viewModel.getMaze() != null) //make sure the maze already exists
            PlayerDisplayer(characterPositionRow,characterPositionColumn,viewModel.getMaze());//char position
    }

    public void setWall(String wallPath) {
        mazeDisplayer.setImageFileNameWall(wallPath);
        if(viewModel.getMaze() != null) //make sure the maze already exists
            MazeDisplayer(viewModel.getMaze());//redraw the maze
    }



    //if the gamer got to the end point, play music
    public void victoryMusic(){
        String bip = ".\\Resources/finish.wav";
        Media hit = new Media(new File(bip).toURI().toString());
        viewController.mediaPlayer = new MediaPlayer(hit);
        viewController.mediaPlayer.play();
    }

    //this function close the stage of the game
    public void closeStage() {
        stage.close();
    }


}

