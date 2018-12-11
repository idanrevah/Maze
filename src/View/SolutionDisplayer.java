package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * this is the solution display class
 * @authors ben moalem & idan revah
 */

public class SolutionDisplayer extends Canvas {
    Maze maze;
    private Solution solution;
    private double canvasHeight;
    private double canvasWidth;
    private double cellHeight;
    private double cellWidth;

    GraphicsContext graphicsContext;

    public void setSolution(Solution solution,Maze maze) {
        this.maze = maze;
        this.solution = solution;
        redrawSolution();
    }

    private void redrawSolution() {
        try {
            Image pathImage = new Image(new FileInputStream(ImageFileNamePath.get()));
            graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, getWidth(), getHeight());
            setSizes();
            //Draw Solution
            ArrayList<AState> solutionPath = solution.getSolutionPath();
            int solLength = solutionPath.size();
            int howManyDraw=0;
            //drawing the solution
            for (AState currState : solutionPath) {
                if (howManyDraw == 0){
                    howManyDraw++;
                    continue;
                }
                // to check that we wont draw on the goal
                if (howManyDraw + 1 < solLength) {
                    int x = ((MazeState) currState).getStatePosition().getRowIndex();
                    int y = ((MazeState) currState).getStatePosition().getColumnIndex();
                    Thread.sleep(80);
                    graphicsContext.drawImage(pathImage, x * cellHeight, y * cellWidth, cellHeight, cellWidth);
                    howManyDraw++;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear the Solution if a new maze is being generated
     */
    public void solutionClearRect(){
        if(graphicsContext != null)
            graphicsContext.clearRect(0, 0, getWidth(), getHeight());
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


    // Properties
    private StringProperty ImageFileNamePath = new SimpleStringProperty();

    //Set and Get Path file
    public void setImageFileNamePath(String imageFileNamePath) {
        this.ImageFileNamePath.set(imageFileNamePath);
    }

    public String getImageFileNamePath() {
        return this.ImageFileNamePath.get();
    }
}