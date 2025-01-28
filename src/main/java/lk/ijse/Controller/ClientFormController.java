package lk.ijse.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class ClientFormController {

    public VBox msgVBox;
    @FXML
    private ScrollPane ScrollPane;

    @FXML
    private Button btnImage;

    @FXML
    private Button btnSend;



    @FXML
    private TextField txtClientMsg;

    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String message = "";

    @FXML
    public void initialize() {
        try {


            if (msgVBox!= null) {
                msgVBox.setSpacing(10);
                ScrollPane.setContent(msgVBox);
                ScrollPane.setFitToWidth(true);

                msgVBox.heightProperty().addListener((observable, oldValue, newValue) ->
                        ScrollPane.setVvalue(1.0));
            } else {
                System.err.println("Error: messageVBox is null. Check FXML file for proper fx:id");
                return;
            }

            new Thread(() -> {
                try {
                    socket = new Socket("localhost", 3000);
                    Platform.runLater(() -> addMessage("You are Connected"));

                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    while (!message.equals("Exit")) {
                        message = dataInputStream.readUTF();

                        if (message.startsWith("[IMAGE]")) {
                            String imagePath = message.substring(7);
                            Platform.runLater(() -> {
                                displayImage(imagePath);
                                addMessage("Server: [Image Received]");
                            });
                        } else {
                            Platform.runLater(() -> addMessage("Server: " + message));
                        }
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> addMessage("Error: Server not found or disconnected"));
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayImage(String imagePath) {
        try {
            File file = new File(imagePath);
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            msgVBox.getChildren().add(imageView);
        } catch (Exception e) {
            addMessage("Error loading image");
            e.printStackTrace();
        }
    }

    private void addMessage(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px;");
        label.setWrapText(true);
        msgVBox.getChildren().add(label);
    }

    @FXML
    void btnImageOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                displayImage(selectedFile.getPath());
                dataOutputStream.writeUTF("[IMAGE]" + selectedFile.getPath());
                dataOutputStream.flush();
                addMessage("Server: [Image Sent]");
            } catch (IOException e) {
                addMessage("Error: Failed to send image");
                e.printStackTrace();
            }
        }
    }

    @FXML
    void btnSendOnAction(ActionEvent event) {
        try {
            String message = txtClientMsg.getText().trim();
            if (!message.isEmpty()) {
                dataOutputStream.writeUTF(message);
                dataOutputStream.flush();
                addMessage("You: " + message);
                txtClientMsg.clear();
            }
        } catch (IOException e) {
            addMessage("Error: Failed to send message");
            e.printStackTrace();
        }
    }

    @FXML
    void txtClientMsgOnAction(ActionEvent event) {

    }

}
