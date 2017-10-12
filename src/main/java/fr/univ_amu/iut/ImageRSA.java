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


/**
 * Created by l16000440 on 05/10/17.
 */

public class ImageRSA extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Load Image");
        StackPane sp = new StackPane();

        Image img = new Image("https://image.noelshack.com/fichiers/2017/41/4/1507800197-mario-transparent-background.png");
        PixelReader reader = img.getPixelReader();
        WritableImage dest = new WritableImage((int) img.getWidth(),(int)img.getHeight());
        PixelWriter writer = dest.getPixelWriter();

        for (int x =0; x < dest.getWidth();++x){
            for (int y =0; y < dest.getHeight();++y){
                Color color = reader.getColor(x,y);
                color = new Color(color.getRed(),color.getGreen(),color.getBlue(),color.getOpacity());

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


