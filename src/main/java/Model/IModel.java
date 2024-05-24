package Model;
import algorithms.mazeGenerators.Maze;
import algorithms.search.*;
import java.util.Observer;

public interface IModel {
    void generateMaze(int rows, int cols);
    int getIndexRowPlayer();
    void solveMaze();
    void setMyMazeMap(Maze maze1);
    int getIndexColumnPlayer();
    Maze getMyMazeMap();
    void updatePlayerLocation(MovementDirection direction);
    Solution getSolution();
    void assignObserver(Observer o);

}
