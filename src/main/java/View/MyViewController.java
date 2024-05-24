package View;
import Server.*;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.net.URL;
import java.util.*;

public class MyViewController implements Initializable,IView, Observer {
    public MazeDisplayer mazeDisplayer;
    public MyViewModel viewModel;
    public AnchorPane ComboBox;
    public ComboBox generate_box;
    public ComboBox solve_box;
    public VBox optionGameMenu;
    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public Label indexRowPlayer;
    public Label indexColumnPlayer;
    public ComboBox playerBox;
    public ComboBox thread_box;
    public AnchorPane paneWelcome;
    public Button button_restartMaze;
    public ToggleButton musicPlayPause;;
    public MenuItem saveMenu;
    public MenuItem loadMenu;
    private MediaPlayer mediaPlayer;
    public Button button_solveMaze;
    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerColumn = new SimpleStringProperty();
    private final Logger LOG = LogManager.getLogger();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        indexRowPlayer.textProperty().bind(updatePlayerRow);
        indexColumnPlayer.textProperty().bind(updatePlayerColumn);
    }


    public void generateMaze(ActionEvent actionEvent) {
        String rowsString = textField_mazeRows.getText();
        String colsString = textField_mazeColumns.getText();

        try {
            int rows = Integer.parseInt(rowsString);
            int cols = Integer.parseInt(colsString);

            if (rows < 5 || cols < 5) {
                showErrorAlert("Rows and columns should be bigger than 5");
            } else {
                viewModel.generateMaze(rows, cols);
                enableControls();
            }
        } catch (NumberFormatException nfe) {
            showErrorAlert("Please enter a positive number");
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(message);
        alert.show();
        textField_mazeRows.setText("");
        textField_mazeColumns.setText("");
    }

    private void enableControls() {
        saveMenu.setDisable(false);
        loadMenu.setDisable(false);
        button_restartMaze.setDisable(false);
        button_solveMaze.setDisable(false);
        playerBox.setDisable(false);
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
        ChangePlayer();
        Media media = new Media((new File("resources/Music/TotalMusic.mp3")).toURI().toString());

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
                                        @Override
                                        public void run() {
                                            mediaPlayer.seek(Duration.ZERO);
                                        }
                                    }
        );
        mediaPlayer.play();
    }

    private void ChangePlayer() {
        playerBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)->{
            if(newVal != null && newVal.equals("Tom")){
                mazeDisplayer.setImageFileNamePlayer("Tom");
                mazeDisplayer.draw();
            }
        });
        playerBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)->{
            if(newVal != null && newVal.equals("Jerry")){
                mazeDisplayer.setImageFileNamePlayer("Jerry");
                mazeDisplayer.draw();
            }
        });
        LOG.info("The played has been changed");
    }

    public void solveMaze(ActionEvent actionEvent) {
        viewModel.solveMaze();
    }


    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent);
        keyEvent.consume();
    }

    public void pauseMusic() {}

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change) {
            case "maze generated" -> mazeGenerated();
            case "player moved" -> playerMoved();
            case "maze solved" -> mazeSolved();
            default -> System.out.println("Not implemented change: " + change);
        }
    }

    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
    }


    public void onMouseDragged(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        double cellHeight = mazeDisplayer.getCellHeight();
        double cellWidth = mazeDisplayer.getCellWidth();
        int playerRow = Integer.parseInt(indexRowPlayer.getText());
        int playerColumn = Integer.parseInt(indexColumnPlayer.getText());
        int moveColumn = (int) (x / cellWidth);
        int moveRow = (int) (y / cellHeight);

        if (playerRow < moveRow && playerRow + 2 > moveRow && playerColumn + 1 > moveColumn && playerColumn - 1 < moveColumn) {
            viewModel.movePlayerByDrag("DOWN");
        }
        else if (playerRow > moveRow && playerRow - 2 < moveRow && playerColumn + 1 > moveColumn && playerColumn - 1 < moveColumn) {
            viewModel.movePlayerByDrag("UP");
        }
        else if (playerRow > moveRow &&  playerRow - 2 <moveRow  && playerColumn<moveColumn && playerColumn + 2 > moveColumn){
            viewModel.movePlayerByDrag("UPRIGHT");
        }
        else if(playerRow < moveRow && playerRow + 2 > moveRow && playerColumn > moveColumn && playerColumn - 2 < moveColumn) {
            viewModel.movePlayerByDrag("DOWNLEFT");
        }
        else if(playerRow-1 < moveRow && playerRow + 1 > moveRow  && playerColumn < moveColumn && playerColumn + 2 >moveColumn){
            viewModel.movePlayerByDrag("RIGHT");
        }
        else if(playerRow > moveRow && moveRow > playerRow - 2 && playerColumn > moveColumn && playerColumn - 2 < moveColumn) {
            viewModel.movePlayerByDrag("UPLEFT");
        }
        else if(playerRow-1 < moveRow && playerRow+1 > moveRow  && playerColumn > moveColumn && playerColumn - 2 < moveColumn){
            viewModel.movePlayerByDrag("LEFT");
        }
        else if (playerRow < moveRow && playerRow + 2 > moveRow   && playerColumn<moveColumn && playerColumn+2 > moveColumn){
            viewModel.movePlayerByDrag("DOWNRIGHT");
        }
    }


    private void playerMoved() {
        setPlayerPosition(viewModel.getPlayerRow(), viewModel.getPlayerCol());
    }



    public void saveMaze(ActionEvent actionEvent) {
        String mazeString=mazeDisplayer.getMaze().toString();
        int hash = mazeString.hashCode();

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Mazes", "*.maze"));
            fileChooser.setInitialDirectory(new File("./resources/Mazes"));
            File file=fileChooser.showSaveDialog(null);
            if (file != null) {
                ObjectOutputStream SolOut = null;
                SolOut = new ObjectOutputStream(new FileOutputStream(file));
                SolOut.writeObject(mazeDisplayer.getMaze());
            }

        }
     catch (IOException e) {
        LOG.error("Exception in saving maze: ",e);
    }
        LOG.info("The maze has been saved");

    }

    public void loadMaze(ActionEvent actionEvent) {
        try {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze"));
        fc.setInitialDirectory(new File("./resources/Mazes"));
        File file = fc.showOpenDialog(null);
        if (file != null) {
            ObjectInputStream InSol = null;

            InSol = new ObjectInputStream(new FileInputStream(file));
            Maze maze1 = (Maze) InSol.readObject();
            mazeDisplayer.setMaze(maze1);
            textField_mazeRows.setText(String.valueOf(maze1.getRows()));
            textField_mazeColumns.setText(String.valueOf(maze1.getColumns()));
            viewModel.setMaze(maze1);
            saveMenu.setDisable(false);
            button_restartMaze.setDisable(false);
            button_solveMaze.setDisable(false);

            playerBox.setDisable(false);
            LOG.info("The maze has been loaded");
        }
        } catch (IOException | ClassNotFoundException e) {
            LOG.error("exception in loading maze: ",e);
        }

    }


    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public String getUpdatePlayerColumn() {
        return updatePlayerColumn.get();
    }

    public void setUpdatePlayerColumn(int updatePlayerColumn) {
        this.updatePlayerColumn.set(updatePlayerColumn + "");
    }


    private void mazeGenerated() {
        mazeDisplayer.drawMaze(viewModel.getMaze());
    }

    public void newMaze(ActionEvent actionEvent) {
        this.generateMaze(actionEvent);
    }

    public void setPlayerPosition(int row, int col) {
        mazeDisplayer.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerColumn(col);
        if ((Integer.parseInt(indexRowPlayer.getText()) == (mazeDisplayer.getMaze().getGoalPosition().getRowIndex())) && (Integer.parseInt(indexColumnPlayer.getText()) == (mazeDisplayer.getMaze().getGoalPosition().getColumnIndex()))) {
            LOG.info("The player arrived to the end of the maze - goal position");
            mediaPlayer.pause();
            Media winMedia = new Media((new File("resources/Music/winningMusic.mp3")).toURI().toString());
            MediaPlayer winMediaPlayer = new MediaPlayer(winMedia);
            winMediaPlayer.setOnEndOfMedia(() -> winMediaPlayer.seek(Duration.ZERO));
            winMediaPlayer.play();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            Image image = new Image("https://cdn-icons-png.flaticon.com/512/2374/2374896.png");
            ImageView imageView = new ImageView(image);
            alert.setGraphic(imageView);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            alert.setTitle("YOU WIN!");
            alert.setHeaderText("CONGRATULATIONS! You solve this maze and got the cheese!");
            alert.setContentText("Do you want to play a new game?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                int rows=0,cols =0;
                String rowsString = textField_mazeRows.getText();
                String colsString = textField_mazeColumns.getText();
                try {
                    rows= Integer.parseInt(rowsString);
                    cols = Integer.parseInt(colsString);
                    if(rows>=5 && cols>=5) {
                        viewModel.generateMaze(rows, cols);
                        winMediaPlayer.stop();
                        if(!musicPlayPause.isSelected())
                            mediaPlayer.play();
                    }
                    else{
                        showErrorAlert("Rows and columns should be bigger then 5");
                    }
                }
                catch (NumberFormatException nfe) {
                    showErrorAlert("Please enter a positive number");
                }
            }
            else{
                if(!musicPlayPause.isSelected()){
                    winMediaPlayer.stop();
                    mediaPlayer.play();

                }
            }
        }
    }

    public void ScrollStarted(ScrollEvent scrollEvent) {
        double y = scrollEvent.getDeltaY();
        if (scrollEvent.isControlDown()) {
            mazeDisplayer.setHeight(mazeDisplayer.getHeight() + y);
            mazeDisplayer.setWidth(mazeDisplayer.getWidth() + y);
            if (mazeDisplayer.getMaze() != null) {
                mazeDisplayer.draw();
            }
        }
    }

    public void resizeWidth(double delta) {
        mazeDisplayer.setWidth(mazeDisplayer.getWidth() + delta);
        if (mazeDisplayer.getMaze() != null) {
            mazeDisplayer.draw();
        }
    }

    public void resizeHeight(double delta) {
        mazeDisplayer.setHeight(mazeDisplayer.getHeight() + delta);
        if (mazeDisplayer.getMaze() != null) {
            mazeDisplayer.draw();
        }

    }

    public void setMazeDisplayerVisible(ActionEvent actionEvent) {
        paneWelcome.setVisible(false);
        mazeDisplayer.setVisible(true);
        optionGameMenu.setVisible(true);
        loadMenu.setDisable(false);
        playerBox.getItems().clear();
        playerBox.getItems().addAll("Tom","Jerry");
    }

    public void restartMaze(ActionEvent actionEvent) {
        this.setPlayerPosition(0,0);
        viewModel.movePlayer(0,0);
        mazeDisplayer.setSolution(null);
        LOG.info("The maze has been restarted");
    }

    public void PlayPause(ActionEvent actionEvent) {
        if(musicPlayPause.isSelected()){
            mediaPlayer.pause();
        }
        else{
            mediaPlayer.play();
        }
    }


   //* options*//

    public void changeProperties(ActionEvent actionEvent) {
        ComboBox.setVisible(true);
        paneWelcome.setVisible(false);
        generate_box.getItems().clear();
        generate_box.getItems().addAll("My Maze Generator", "Simple Maze Generator", "Empty Maze Generator");
        solve_box.getItems().clear();
        solve_box.getItems().addAll("Depth First Search", "BestFirst Search", "Bread thFirst Search");
        thread_box.getItems().clear();
        thread_box.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    }

    public void OK(ActionEvent actionEvent) {
        Configurations con = Configurations.getInstance();
        con.changeProperties();
        ComboBox.setVisible(false);
        if (!mazeDisplayer.isVisible()){
            paneWelcome.setVisible(true);
        }
        LOG.info("Properties has been changed");
    }

   public void About(ActionEvent actionEvent) {
       Alert alert = new Alert(Alert.AlertType.INFORMATION);
       alert.setTitle("About");
       Image image = new Image("https://images.freeimages.com/fic/images/icons/138/toolbar/256/about.png");
       ImageView imageView = new ImageView(image);

       imageView.setFitWidth(90);
       imageView.setFitHeight(60);
       alert.setGraphic(imageView);
       alert.setHeaderText("Welcome to Tom&Jerry's Maze Adventure.\n" + "Linoy Bitan and Noam Barak are the creator of the .\n" +
               "Choose from three maze generation options: \n" + " 1) Empty Maze Generator algorithm \n" +
               " 2) Simple Maze Generator algorithm\n" + " 3) My Maze Generator algorithm\n" +
               "To build our maze we used Prim's algorithm.\n" + "There are three option to solve the maze: \n" +
               " 1) Depth First Search algorithm\n" + " 2) Best First Search algorithm\n" + " 3) Breadth First Search algorithm\n" +
               "Your objective is to reach the cheese. Good luck and enjoy the adventure!");
       alert.show();
       LOG.info("About");
   }

    public void Exit(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        Image image = new Image("https://w7.pngwing.com/pngs/405/353/png-transparent-exit-sign-emergency-exit-computer-icons-wooden-miscellaneous-angle-furniture.png");
        ImageView imageView = new ImageView(image);
        alert.setGraphic(imageView);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        alert.setHeaderText("Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            viewModel.stopServers();
            System.exit(0);
        } else {
            actionEvent.consume();
        }

    }


    public void Help(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        Image image = new Image("https://cdn-icons-png.flaticon.com/512/3038/3038144.png");
        ImageView imageView = new ImageView(image);
        alert.setGraphic(imageView);;
        imageView.setFitWidth(70);
        imageView.setFitHeight(60);
        alert.setHeaderText("Welcome to Tom&Jerry's Maze Adventure!\n" +
                "To begin the game, you need to click on \"New Game\" and enter the desired number of rows and columns.\n" +
                "Once you've set the dimensions, click on \"Generate Maze\" to create a unique maze.\n" +
                "If you ever get stuck and need help, click on \"Solve Maze\" to reveal the solution.\n" +
                "Feel free to customize the game settings by selecting \"Options\" from the menu.\n" +
                "You can also save your progress by choosing \"Save Maze\" in the \"File\" menu.\n" +
                "Get ready for an exciting adventure through the maze!\n" +
                "Have a great time playing!");
        alert.show();
        LOG.info("Help");
    }


}