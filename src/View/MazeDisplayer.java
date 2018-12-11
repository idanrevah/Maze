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
 * this is the maze displayer class
 * @authors ben moalem & idan revah
 */


public class MazeDisplayer extends Canvas {
    private Maze maze;

    private double canvasHeight;
    private double canvasWidth;
    private double cellHeight;
    private double cellWidth;

    public void setMaze(Maze maze) {
        this.maze = maze;
        setSizes();
        redrawMaze();
    }

    public void redrawMaze() {
        if (maze != null) {
            setSizes();
            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image startImage = new Image(new FileInputStream(ImageFileNameStart.get()));
                Image finishImage = new Image(new FileInputStream(ImageFileNameFinish.get()));

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());

                //Draw Maze
                for (int i = 0; i < maze.get_Maze().length; i++) {
                    for (int j = 0; j < maze.get_Maze()[0].length; j++) {
                        if (maze.valueAt(i,j) == 1) {
                            //gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            gc.drawImage(wallImage, i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                        }
                        else if(maze.isStartingPoint(i,j)){
                            gc.drawImage(startImage, i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                        }
                        else if(maze.isEndingPoint(i,j)){
                            gc.drawImage(finishImage, i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Set the sizes of the canvas Height and Width
     */
    private void setSizes(){
        this.canvasHeight = getHeight();
        this.canvasWidth = getWidth();
        this.cellHeight = canvasHeight / maze.get_Row();
        this.cellWidth = canvasWidth / maze.get_Column();
    }

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameStart = new SimpleStringProperty();
    private StringProperty ImageFileNameFinish = new SimpleStringProperty();



    //---------------------//
    //------Set & Get------//
    //---------------------//
    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNameStart(String imageFileNameStart) {
        this.ImageFileNameStart.set(imageFileNameStart);
    }

    public void setImageFileNameFinish(String imageFileNameF) {
        this.ImageFileNameFinish.set(imageFileNameF);
    }

    public String getImageFileNameWall() {
        return this.ImageFileNameWall.get();
    }

    public String getImageFileNameStart() {
        return this.ImageFileNameStart.get();
    }

    public String getImageFileNameFinish() {
        return this.ImageFileNameFinish.get();
    }

}
