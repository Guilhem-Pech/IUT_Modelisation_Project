package fr.univ_amu.iut;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by l16000440 on 05/10/17.
 */

public class ImageRSA extends Application {

    private Color encodeColor(Color color,int toEncode){
        int encodeToR = toEncode & 0b11100000000;
        int encodeToG = toEncode & 0b00011100000;
        int encodeToB = toEncode & 0b00000011100;
        int encodeToA = toEncode & 0b00000000001;

        double red = ((int)(color.getRed()*255) & 0b11111000) + encodeToR;
        double green = ((int)(color.getGreen()*255) & 0b11111000) + encodeToG;
        double blue = ((int)(color.getBlue()*255) & 0b11111000) + encodeToB;
        double alpha = ((int)(color.getOpacity()*255) & 0b11111110) + encodeToA;


        return new Color(red/255,green/255,blue/255,alpha/255);
    }

    private BigInteger decodeColor(Color encodedColor){
        int red = (int)(encodedColor.getRed() * 255) << 5;
        int green  = (int)(encodedColor.getGreen() * 255) << 5;
        int blue = (int)(encodedColor.getBlue() * 255) << 5;
        int alpha = (int)(encodedColor.getOpacity() * 255) << 7;
        

        return  BigInteger.TEN;

    }

    @Override
    public void start(Stage primaryStage) {
        MessageRSA message = new MessageRSA("Ceci est un message de test les amis !");
        ArrayList<BigInteger> codedMessage = (ArrayList<BigInteger>) message.getCryptedMessage();

        primaryStage.setTitle("Load Image");
        StackPane sp = new StackPane();

        Image img = new Image("https://image.noelshack.com/fichiers/2017/41/4/1507800197-mario-transparent-background.png");
        PixelReader reader = img.getPixelReader();
        WritableImage dest = new WritableImage((int) img.getWidth(),(int)img.getHeight());
        PixelWriter writer = dest.getPixelWriter();

        for (int x =0; x < dest.getWidth();++x){
            for (int y =0; y < dest.getHeight();++y){
                int charToCode = 0;
                if ((x + 1) * (y + 1) <= codedMessage.size() - 1){
                    System.out.println((x + 1) * (y + 1));
                    codedMessage.get((x + 1) * (y + 1)).intValue();
                }

                Color color = reader.getColor(x,y);
                color = this.encodeColor(color,charToCode);

                writer.setColor(x,y,color);
            }
        }

        ImageView imgView = new ImageView(dest);

        saveToFile(img);
        sp.getChildren().add(imgView);
        //Adding HBox to the scene
        Scene scene = new Scene(sp);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public static void saveToFile(Image image) {
        File outputFile = new File("Mario");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}


