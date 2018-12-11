package View;

import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;


import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * this is the player displayer class
 * @authors ben moalem & idan revah
 */

public class PlayerDisplayer extends Canvas {
    private Maze maze;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;

    private double canvasHeight;
    private double canvasWidth;
    private double cellHeight;
    private double cellWidth;

    public void setCharacterPosition(int row, int column, Maze maze) {
        this.maze = maze;
        characterPositionRow = row;
        characterPositionColumn = column;
        redrawCharacter();
    }

    private void redrawCharacter() {
        try{
            Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());
            setSizes();

            //Draw Character
            gc.drawImage(characterImage, characterPositionRow * cellHeight, characterPositionColumn * cellWidth, cellHeight, cellWidth);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    /**
     * Set the sizes of the canvas Height and Width
     */
    private void setSizes(){
        this.canvasHeight = getHeight();
        this.canvasWidth = getWidth();
        this.cellHeight = canvasHeight / this.maze.get_Row();
        this.cellWidth = canvasWidth / this.maze.get_Column();
    }

    //region Properties
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();

    //Set and Get Wall file
    public void setImageFileNameCharacter(String imageFileNamechar) {
        this.ImageFileNameCharacter.set(imageFileNamechar);
    }

    public String getImageFileNameCharacter() {
        return this.ImageFileNameCharacter.get();
    }
    //endregion
}
