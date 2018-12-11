package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;


/**
 * this is the interface of all Views
 * @authors ben moalem & idan revah
 */

public interface IView {
    void MazeDisplayer(Maze maze);
    void SolutionDisplayer(Solution solution, Maze maze);
    void PlayerDisplayer(int characterPositionRow, int characterPositionColumn, Maze maze);
}
