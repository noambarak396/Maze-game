package View;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas {
    private Maze Maze;
    private Solution solution;
    private int indexRowPlayer = 0;
    private int indexColumnPlayer = 0;
    private double cellWidth = 0;
    private double cellHeight = 0;
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameSolution = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();

    public MazeDisplayer(){

    }
    public void setMaze(Maze maze) {
        Maze = maze;
        draw();
    }
    public Maze getMaze(){
        return this.Maze;
    }
    public int getIndexRowPlayer() {
        return indexRowPlayer;
    }
    public int getIndexColumnPlayer() {
        return indexColumnPlayer;
    }

    public void setPlayerPosition(int row, int col) {
        this.indexRowPlayer = row;
        this.indexColumnPlayer = col;
        draw();
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        draw();
    }

    public void drawMaze(Maze maze) {
        this.Maze = maze;
        solution = null;
        draw();
    }

    public void draw() {
        if(Maze.getMaze_map() != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = Maze.getMaze_map().length;
            int cols = Maze.getMaze_map()[0].length;

            this.cellHeight = canvasHeight / rows;
            this.cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            drawMazeWalls(graphicsContext, cellHeight, cellWidth, rows, cols);
            if(solution != null) {
                drawSolution(graphicsContext, cellHeight, cellWidth);
            }
            drawPlayer(graphicsContext, cellHeight, cellWidth);
        }
    }


    private void drawSolution(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {

        if (solution != null){
            Image solutionImage = null;
            try{
                solutionImage = new Image(new FileInputStream(getImageFileNameSolution()));

            } catch (FileNotFoundException e) {
                System.out.println("There is no solution image file");
            }
            graphicsContext.setFill(Color.WHITE);
            ArrayList<AState> statesList = solution.getSolutionPath();
            for (int index = 0; index < statesList.size() -1 ; index++) {
                MazeState mazeState = (MazeState) statesList.get(index);
                int rowIndex = mazeState.getCurrent_position().getRowIndex();
                int columnIndex = mazeState.getCurrent_position().getColumnIndex();
                double x = columnIndex * cellWidth;
                double y = rowIndex * cellHeight;
                if (solutionImage == null)
                    graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                else {
                    graphicsContext.drawImage(solutionImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    private void drawMazeWalls(GraphicsContext graphicsContext, double cellHeight, double cellWidth, int rows, int cols) {
        graphicsContext.setFill(Color.RED);

        Image wallImage = null;
        Image goalImage = null;

        try{
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
            goalImage = new Image(new FileInputStream(getImageFileNameGoal()));

        } catch (FileNotFoundException e) {
            System.out.println("There is no wall or goal image file");
        }

        int goalRow = Maze.getGoalPosition().getRowIndex();
        int goalCol = Maze.getGoalPosition().getColumnIndex();

        if(goalImage != null) {
            graphicsContext.drawImage(goalImage, goalCol * cellWidth, goalRow * cellHeight, cellWidth, cellHeight);
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(Maze.getMaze_map()[i][j] == 1){
                    //if it is a wall:
                    double x = j * cellWidth;
                    double y = i * cellHeight;
                    if (wallImage == null)
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getIndexColumnPlayer() * cellWidth;
        double y = getIndexRowPlayer() * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image file");
        }
        if(playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }

    /* get and set functions*/

    public String getImageFileNameSolution() { return imageFileNameSolution.get(); }

    public String getImageFileNameGoal() { return imageFileNameGoal.get(); }

    public String getImageFileNameWall() { return imageFileNameWall.get(); }

    public String getImageFileNamePlayer() { return imageFileNamePlayer.get(); }

    public double getCellWidth() { return cellWidth; }

    public double getCellHeight() { return cellHeight; }

    @Override
    public boolean isResizable() {return true;}

    @Override
    public double prefHeight(double width) {return getHeight();}

    @Override
    public double prefWidth(double height) {return getWidth();}

    public void setImageFileNameWall(String imageFileNameWall) { this.imageFileNameWall.set(imageFileNameWall); }
    public void setImageFileNameGoal(String imageFileNameGoal) { this.imageFileNameGoal.set(imageFileNameGoal); }
    public StringProperty imageFileNameGoalProperty() { return imageFileNameGoal; }
    public void setImageFileNameSolution(String imageFileNameSolution) { this.imageFileNameSolution.set(imageFileNameSolution); }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        String Jerry = "./resources/images/Jerry.jpg";
        String Tom = "./resources/images/Tom.png";
        switch (imageFileNamePlayer){
            case "Jerry" ->  this.imageFileNamePlayer.set(Jerry);
            case "Tom" ->  this.imageFileNamePlayer.set(Tom);
            default -> {
                this.imageFileNamePlayer.set(Tom);
            }
        }
    }

}
