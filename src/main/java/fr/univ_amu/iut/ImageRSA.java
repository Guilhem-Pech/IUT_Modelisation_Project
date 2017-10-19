package fr.univ_amu.iut;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileCacheImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;


/**
 * Created by l16000440 on 05/10/17.
 */

public class ImageRSA extends Application {

    public static int[] getIntARGB(int rgb){
        int argb[] = new int[4];
        argb[0] = (rgb >> 24) & 0xFF;
        argb[1] =   (rgb >> 16) & 0xFF;
        argb[2] = (rgb >>  8) & 0xFF;
        argb[3] =  (rgb) & 0xFF;

        return argb;
    }
    public static int getARBGInt(int a, int r, int g, int b) {
        return ((a << 24) | 0xFF) + ((r << 16) | 0xFF) + ((g << 8) | 0xFF) + (b | 0xFF);
    }

    private int encodeColor(int color, int toEncode){

        int encodeToR = (toEncode & 0b11100000000) >> 8;
        int encodeToG = (toEncode & 0b00011100000) >> 5;
        int encodeToB = (toEncode & 0b00000011100) >> 2;
        int encodeToA = (toEncode & 0b00000000001);



        int[] argb = getIntARGB(color);
        argb[0] = argb[0] & 0b11111110 + encodeToA;
        argb[1] = argb[1] & 0b11111000 + encodeToR;
        argb[2] = argb[2] & 0b11111000 + encodeToG;
        argb[3] = argb[3] & 0b11111000 + encodeToB;


        return getARBGInt(argb[0],argb[1],argb[2],argb[3]);

    }

    private BigInteger decodeColor(int color){
        int[] argb = getIntARGB(color);
        int red = argb[1];
        int green = argb[2];
        int blue = argb[3];
        int alpha = argb[0];

        int codedOnR = (red & 0b00000111) << 8;
        int codedOnG = (green & 0b00000111) << 5;
        int codedOnB = (blue & 0b00000111) << 2;
        int codedOnA = (alpha & 0b00000001);


        System.out.println(codedOnG+ " to decode");

        return BigInteger.valueOf(codedOnR + codedOnG + codedOnB + codedOnA);

    }

    @Override
    public void start(Stage primaryStage) {
        MessageRSA message = new MessageRSA("Ceci est un message de test les amis !");
        ArrayList<BigInteger> codedMessage = (ArrayList<BigInteger>) message.getCryptedMessage();

        primaryStage.setTitle("Load Image");
        StackPane sp = new StackPane();

        Image img = new Image("http://www.fnordware.com/superpng/pnggrad8rgb.png");
        PixelReader reader = img.getPixelReader();
        WritableImage dest = new WritableImage((int) img.getWidth(),(int)img.getHeight());
        PixelWriter writer = dest.getPixelWriter();
        int charToCode = 0;
        for (int x =0; x < dest.getWidth();++x){
            for (int y =0; y < dest.getHeight();++y){
                Color color = reader.getColor(x,y);
                if ((x*dest.getWidth() + y)<= codedMessage.size() - 1){
                    charToCode = codedMessage.get((int) (x * dest.getWidth() + y)).intValue();
                    int argb = reader.getArgb(x,y);
                    int encodedargb = this.encodeColor(argb,charToCode);
                    writer.setArgb(x,y,encodedargb);

                }else
                writer.setColor(x,y,Color.BEIGE);

            }
        }

        ImageView imgView = new ImageView(dest);

        saveToFile(dest);
        sp.getChildren().add(imgView);
        //Adding HBox to the scene
        Scene scene = new Scene(sp);
        primaryStage.setScene(scene);
        primaryStage.show();


        Image todecrypt = new Image("file:Mario");
        PixelReader pixelReader = todecrypt.getPixelReader();
        ArrayList<BigInteger> code = new ArrayList<>();
        for (int x =0; x < 1;++x) {
            for (int y = 0; y < message.getUncryptedMessage().length(); ++y) {
                int argb = pixelReader.getArgb(x,y);
                code.add(decodeColor(argb));
            }
        }
        MessageRSA messageRSA = new MessageRSA(code,message.getPrivateKeys()[0],message.getPrivateKeys()[1]);


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


