package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.File;

/**
 * this is the interface of all Models
 * @authors ben moalem & idan revah
 */

public interface IModel {
    void generateMaze(int rows, int columns);
    Maze getMaze();
    void startServers();
    void stopServers();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    void solveMaze();
    void moveCharacter(KeyCode direction);
    Solution getSolution();

    void loadMaze(File file);

    void saveCurrentMaze(File file);
}
