package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.util.Observable;
import java.util.Observer;


/**
 * this is the View Model Class
 * he connect between the View and the Model
 * @authors  idan revah & ben moalem
 */

public class MyViewModel extends Observable implements Observer {
    private IModel model;

    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;

    public StringProperty characterPositionRow = new SimpleStringProperty("1"); //For Binding
    public StringProperty characterPositionColumn = new SimpleStringProperty("1"); //For Binding

    public MyViewModel(IModel model){
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){

            //if it's loaded maze
            if(arg != null){
                Position startPosition = model.getMaze().getStartPosition();
                characterPositionRowIndex = startPosition.getRowIndex();
                characterPositionRow.set(characterPositionRowIndex + "");
                characterPositionColumnIndex = startPosition.getColumnIndex();
                characterPositionColumn.set(characterPositionColumnIndex + "");
            }
            else {//if it's new generated maze
                characterPositionRowIndex = model.getCharacterPositionRow();
                characterPositionRow.set(characterPositionRowIndex + "");
                characterPositionColumnIndex = model.getCharacterPositionColumn();
                characterPositionColumn.set(characterPositionColumnIndex + "");
            }
            setChanged();
            notifyObservers();
        }
    }


    //--------------------------//
    //---Moving the calls to----//
    //-----the Model class------//
    //--------------------------//

    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public void solveMaze() {
        model.solveMaze();
    }

    public Solution getSolution(){
        return model.getSolution();
    }

    public void exit(){
        model.stopServers();
    }

    public void loadFile(File file) { model.loadMaze(file); }

    public void saveCurrentMaze(File file) { model.saveCurrentMaze(file); }
}
