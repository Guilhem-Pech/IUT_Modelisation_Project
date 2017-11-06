package fr.univ_amu.iut;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;


/**
 * Created by l16000440 on 05/10/17.
 */

public class InterfaceGraphique extends Application {
    private static String endMessage = "/endMessage";
    private final ImageRSA ImageRSA = new ImageRSA();

    @FXML
    private TextField privateGenerate;

    @FXML
    private TextField publicGenerate;

    @FXML
    private TextField pathEncode;

    @FXML
    private TextField publicN;

    @FXML
    private TextField publicE;

    @FXML
    private TextArea textToEncode;

    @FXML
    private TextField pathDecode;

    @FXML
    private TextField privateN;

    @FXML
    private TextField privateD;

    @FXML
    private TextArea decodedText;

    @FXML
    private void initialize() {
    }

    @FXML
    private void handleButtonGenerate(){
        MessageRSA generating = new MessageRSA();
        generating.generateKey();
        privateGenerate.setText(String.valueOf(generating.getPrivateKeys()[0]) + " , " + String.valueOf(generating.getPrivateKeys()[1]));
        publicGenerate.setText(String.valueOf(generating.getPublicKeys()[0]) + " , " + String.valueOf(generating.getPublicKeys()[1]));
    }

    private void choosePNGImage(TextField textField,String title) {
        FileChooser choose = new FileChooser();
        choose.setTitle(title);
        choose.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image","*.png"));
        File choosenFile = choose.showOpenDialog(new Stage());
        if (choosenFile != null && choosenFile.exists() && choosenFile.canRead()){
            textField.setText(choosenFile.getPath());
        }
    }

    @FXML
    private void  handleChooseImageEncode(){
        choosePNGImage(pathEncode,"Choose an image where code the message");
    }

    @FXML
    private void  handleChooseImageDecode(){
        choosePNGImage(pathDecode,"Choose an image to decode");
    }


    @FXML
    private void handleEncodeButton(){
        if(pathEncode.getText().isEmpty()){
            errorMessage("No image chosen !", "Please choose an image before encode");
        } else if (textToEncode.getText().isEmpty()){
            errorMessage("No message to code !", "Please enter a message to code");
        }
        else if (publicE.getText().isEmpty() || publicN.getText().isEmpty()){
            errorMessage("Public key invalid", "Please enter a proper public key");
        } else{
            BigInteger n = new BigInteger(publicN.getText());
            BigInteger e = new BigInteger(publicE.getText());
            MessageRSA message = new MessageRSA(textToEncode.getText(),n,e);
            Image img = new Image("file:"+pathEncode.getText());

            if (img.isError()){
                errorMessage("ERROR",img.getException().toString());
                return;
            }
            WritableImage encryptedImage = ImageRSA.getCryptedWritableImage(message, endMessage, img);
            ImageRSA.saveToFile(encryptedImage,pathEncode.getText(0,pathEncode.getLength()-4)+"_encoded.png");
        }
    }


    @FXML
    private void handleDecodeButton(){
        if(pathDecode.getText().isEmpty()){
            errorMessage("No image chosen !", "Please choose an image before decode");
        }
        else if (privateD.getText().isEmpty() || privateN.getText().isEmpty()){
            errorMessage("Private key invalid", "Please enter a valid private key");
        } else{
            Image imgToDecrypt = new Image ("file:"+pathDecode.getText());
            BigInteger n = new BigInteger(privateN.getText());
            BigInteger d = new BigInteger(privateD.getText());
            MessageRSA messageRSA = ImageRSA.getMessageRSA(n,d, endMessage, imgToDecrypt);
            decodedText.setText(messageRSA.getUncryptedMessage());
        }
    }

    private void errorMessage(String headerText, String contentText) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Error");
        error.setHeaderText(headerText);
        error.setContentText(contentText);
        error.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) {
        try {

            final URL url = getClass().getResource("interface.fxml");
            final FXMLLoader fxmlLoader = new FXMLLoader(url);
            final BorderPane root = (BorderPane) fxmlLoader.load();
            final Scene scene = new Scene(root, 850, 495);

            primaryStage.setScene(scene);
        } catch (IOException ex) {
            System.err.println("Erreur au chargement: " + ex);
        }
        primaryStage.setTitle("Modelisation Project");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}


