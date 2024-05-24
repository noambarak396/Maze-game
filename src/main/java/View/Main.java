package View;
import Model.MyModel;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;


public class Main extends Application {

    private MyModel model;


    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Tom&Jerry's Maze");
        primaryStage.setScene(new Scene(root, 750, 600));
        this.SetStageCloseEvent(primaryStage);
        primaryStage.show();
        model = new MyModel();
        model.startServers();
        MyViewModel viewModel = new MyViewModel(model);

        MyViewController myViewController = fxmlLoader.getController();
        myViewController.setViewModel(viewModel);
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double delta= newVal.doubleValue()-oldVal.doubleValue();
            myViewController.resizeWidth(delta);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            double delta= (newVal.doubleValue()-oldVal.doubleValue());
            myViewController.resizeHeight(delta);

        });
    }

    private void SetStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
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
                    model.stopServers();
                    System.exit(0);
                } else {
                    windowEvent.consume();
                }

            }
        });
    }
}
//"https://i.pinimg.com/originals/64/8e/f4/648ef464f47ba6e25f63395656a00039.jpg"
// <properties>
//        <maven.compiler.source>15</maven.compiler.source>
//        <maven.compiler.target>15</maven.compiler.target>
//    </properties>



