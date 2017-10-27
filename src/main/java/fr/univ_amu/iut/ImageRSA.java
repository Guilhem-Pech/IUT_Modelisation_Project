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

    public static int[] getIntARGB(int argb){
        int[] result = new int[4];
        result[0] = (argb & 0xff000000) >> 24;
        result[1] = (argb & 0x00FF0000) >> 16;
        result[2] = (argb & 0x0000FF00) >> 8;
        result[3] = (argb & 0x000000FF);

        return result;
    }
    public static int getARBGInt(int a, int r, int g, int b) {
        return ((a << 24)) + ((r << 16)) + ((g << 8)) + (b);
    }

    private int encodeColor(int color, int toEncode){

        int encodeToR = (toEncode & 0b1110000000) >> 7;
        int encodeToG = (toEncode & 0b0001110000) >> 4;
        int encodeToB = (toEncode & 0b0000001110) >> 1;
        int encodeToA = (toEncode & 0b0000000001);




        int[] argb = getIntARGB(color);
        int intargb = getARBGInt(argb[0],argb[1],argb[2],argb[3]);

        argb[0] = (argb[0] & 0b11111110) + encodeToA;
        argb[1] = (argb[1] & 0b11111000) + encodeToR;
        argb[2] = (argb[2] & 0b11111000) + encodeToG;
        argb[3] = (argb[3] & 0b11111000) + encodeToB;
        System.out.println(toEncode + " coded");
        return getARBGInt(argb[0],argb[1],argb[2],argb[3]);
    }

    private BigInteger decodeColor(int color){
        int[] argb = getIntARGB(color);
        int red = argb[1];
        int green = argb[2];
        int blue = argb[3];
        int alpha = argb[0];
        int codedOnR = (red & 0b00000111) << 7;
        int codedOnG = (green & 0b00000111) << 4;
        int codedOnB = (blue & 0b00000111) << 1;
        int codedOnA = (alpha & 0b00000001);
        System.out.println(codedOnR + codedOnG + codedOnB + codedOnA);
        return BigInteger.valueOf(codedOnR + codedOnG + codedOnB + codedOnA);

    }
    @Override
    public void start(Stage primaryStage) {
        MessageRSA message = new MessageRSA(String.format("Ceci est un message de test les amis"));
        ArrayList<BigInteger> codedMessage = (ArrayList<BigInteger>) message.getCryptedMessage();

        primaryStage.setTitle("Load Image");
        StackPane sp = new StackPane();

        Image img = new Image("https://cdn.pixabay.com/photo/2017/07/11/17/45/sunset-2494419_960_720.png");
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
        System.out.println(messageRSA);

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


