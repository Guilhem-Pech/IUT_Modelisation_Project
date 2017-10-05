package fr.univ_amu.iut;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Created by l16000440 on 05/10/17.
 */
public class image extends Application {


    @Override

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Load Image");
        StackPane sp = new StackPane();

        Image img = new Image("https://image.noelshack.com/fichiers/2017/40/4/1507207673-test.jpg");
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
        sp.getChildren().add(imgView);
        //Adding HBox to the scene
        Scene scene = new Scene(sp);
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}


