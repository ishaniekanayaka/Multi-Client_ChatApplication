package lk.ijse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {

            stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/view/ServerForm.fxml"))));

            Stage stage1 = new Stage();
            stage1.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("/view/ClientForm.fxml"))));



            stage.setTitle("Server Form");
            stage1.setTitle("Client Form");


            stage.centerOnScreen();
            stage1.centerOnScreen();


            stage.show();
            stage1.show();

    }
}
