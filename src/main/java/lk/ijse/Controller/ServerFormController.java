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
import java.net.ServerSocket;
import java.net.Socket;

import static com.sun.javafx.logging.PulseLogger.addMessage;

public class ServerFormController {

    @FXML
    private javafx.scene.control.ScrollPane ScrollPane;

    @FXML
    private Button btnImage;

    @FXML
    private Button btnSend;

    @FXML
    private VBox msgVBox;

    @FXML
    private TextField txtMsg;

    private ServerSocket serverSocket;
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String message = "";

    @FXML
    public void initialize() {
        try {

            msgVBox.setSpacing(10);
            ScrollPane.setContent(msgVBox);
            ScrollPane.setFitToWidth(true);


            msgVBox.heightProperty().addListener((observable, oldValue, newValue) ->
                    ScrollPane.setVvalue(1.0));

            new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(3000);

                    Platform.runLater(() -> addMessage("Server Started. Waiting for client..."));

                    socket = serverSocket.accept();
                    Platform.runLater(() -> addMessage("Client Connected!"));

                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    while (!message.equals("Exit")) {
                        message = dataInputStream.readUTF();

                        if (message.startsWith("[IMAGE]")) {
                            String imagePath = message.substring(7);
                            Platform.runLater(() -> {
                                displayImage(imagePath);
                                addMessage("Client: [Image Received]");
                            });
                        } else {
                            Platform.runLater(() -> addMessage("Client: " + message));
                        }
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> addMessage("Error: Connection lost"));
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
            String message = txtMsg.getText().trim();
            if (!message.isEmpty()) {
                dataOutputStream.writeUTF(message);
                dataOutputStream.flush();
                addMessage("You: " + message);
                txtMsg.clear();
            }
        } catch (IOException e) {
            addMessage("Error: Failed to send message");
            e.printStackTrace();
        }
    }

    @FXML
    void txtMsgOnAction(ActionEvent event) {

    }

}