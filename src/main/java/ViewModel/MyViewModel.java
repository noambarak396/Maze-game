package ViewModel;
import Model.IModel;
import Model.MovementDirection;
import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.*;
import javafx.scene.input.KeyEvent;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this); //Observe the Model for it's changes
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public Maze getMaze(){ return model.getMyMazeMap();}

    public int getPlayerRow(){
        return model.getIndexRowPlayer();
    }

    public int getPlayerCol(){
        return model.getIndexColumnPlayer();
    }

    public Solution getSolution(){
        return model.getSolution();
    }

    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols);
    }

    public void movePlayer(KeyEvent keyEvent){
        MovementDirection direction;
        switch (keyEvent.getCode()){
            case UP -> direction = MovementDirection.UP;
            case DOWN -> direction = MovementDirection.DOWN;
            case LEFT -> direction = MovementDirection.LEFT;
            case RIGHT -> direction = MovementDirection.RIGHT;

            case NUMPAD8 -> direction = MovementDirection.UP;
            case DIGIT8 -> direction = MovementDirection.UP;

            case NUMPAD2 -> direction = MovementDirection.DOWN;
            case DIGIT2 -> direction = MovementDirection.DOWN;

            case NUMPAD4 -> direction = MovementDirection.LEFT;
            case DIGIT4 -> direction = MovementDirection.LEFT;

            case NUMPAD6 -> direction = MovementDirection.RIGHT;
            case DIGIT6 -> direction = MovementDirection.RIGHT;

            case NUMPAD7 -> direction = MovementDirection.UPLEFT ;
            case DIGIT7 -> direction = MovementDirection.UPLEFT ;

            case NUMPAD9 -> direction = MovementDirection.UPRIGHT ;
            case DIGIT9 -> direction = MovementDirection.UPRIGHT ;

            case NUMPAD1 -> direction = MovementDirection.DOWNLEFT ;
            case DIGIT1 -> direction = MovementDirection.DOWNLEFT ;

            case NUMPAD3 -> direction = MovementDirection.DOWNRIGHT ;
            case DIGIT3 -> direction = MovementDirection.DOWNRIGHT ;

            default -> {
                // no need to move the player...
                return;
            }
        }
        model.updatePlayerLocation(direction);
    }

    public void solveMaze(){
        model.solveMaze();
    }

    public void movePlayer(int i, int i1) {
        MyModel m=(MyModel)model;
        m.movePlayer(0,0);
    }

    public void movePlayerByDrag(String move) {
        MovementDirection direction;
        switch (move){
            case "UP" -> direction = MovementDirection.UP;
            case "DOWN" -> direction = MovementDirection.DOWN;
            case "LEFT" -> direction = MovementDirection.LEFT;
            case "RIGHT" -> direction = MovementDirection.RIGHT;
            case "UPRIGHT" -> direction = MovementDirection.UPRIGHT;
            case "DOWNRIGHT" -> direction = MovementDirection.DOWNRIGHT;
            case "UPLEFT" -> direction = MovementDirection.UPLEFT;
            case "DOWNLEFT" -> direction = MovementDirection.DOWNLEFT;
            default -> {
                // no need to move the player...
                return;
            }
        }
        model.updatePlayerLocation(direction);
    }

    public void setMaze(Maze maze1) {

        model.setMyMazeMap(maze1);
    }

    public void stopServers() {
        MyModel m=(MyModel)model;
        m.stopServers();
    }
}
