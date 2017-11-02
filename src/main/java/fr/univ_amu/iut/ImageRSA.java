package fr.univ_amu.iut;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ImageRSA {
    public ImageRSA() {
    }

    /**
     * Decompose the argb level
     * @param argb
     * @return [0] = Alpha ; [1] = Red ; [2] = Green ; [2] = Blue
     */
    public static int[] getIntARGB(int argb) {
        int[] result = new int[4];
        result[0] = (argb & 0xff000000) >> 24;
        result[1] = (argb & 0x00FF0000) >> 16;
        result[2] = (argb & 0x0000FF00) >> 8;
        result[3] = (argb & 0x000000FF);

        return result;
    }

    /**
     * Recompose the ARGB level
     * @param a Alpha
     * @param r Red
     * @param g Green
     * @param b Blue
     * @return
     */
    public static int getARBGInt(int a, int r, int g, int b) {
        return ((a << 24)) + ((r << 16)) + ((g << 8)) + (b);
    }

    /**
     * 'Insert' an int into a pixel
     * @param color color to modify
     * @param toEncode int to hide
     * @return ARGB level of the color with the int inside
     */
    private int encodeColor(int color, int toEncode) {

        int encodeToR = (toEncode & 0b1110000000) >> 7;
        int encodeToG = (toEncode & 0b0001110000) >> 4;
        int encodeToB = (toEncode & 0b0000001110) >> 1;
        int encodeToA = (toEncode & 0b0000000001);

        int[] argb = getIntARGB(color);

        argb[0] = (argb[0] & 0b11111110) + encodeToA;
        argb[1] = (argb[1] & 0b11111000) + encodeToR;
        argb[2] = (argb[2] & 0b11111000) + encodeToG;
        argb[3] = (argb[3] & 0b11111000) + encodeToB;

        return getARBGInt(argb[0], argb[1], argb[2], argb[3]);
    }

    /**
     * Get the int inside a color
     * @param color
     * @return the int hiden
     */
    private BigInteger decodeColor(int color) {
        int[] argb = getIntARGB(color);
        int red = argb[1];
        int green = argb[2];
        int blue = argb[3];
        int alpha = argb[0];
        int codedOnR = (red & 0b00000111) << 7;
        int codedOnG = (green & 0b00000111) << 4;
        int codedOnB = (blue & 0b00000111) << 1;
        int codedOnA = (alpha & 0b00000001);
        return BigInteger.valueOf(codedOnR + codedOnG + codedOnB + codedOnA);

    }

    /**
     * Crypt the message in the image and return an ImageView of it
     * @param message RSA
     * @param endMessage end pattern of a message
     * @param img The image
     * @return
     */
    ImageView getCryptedImageView(MessageRSA message, String endMessage, Image img) throws IOException {
        WritableImage dest = getCryptedWritableImage(message, endMessage, img);
        return new ImageView(dest);
    }

    /**
     * Crypt the message in the image and return an WritableImage of it
     * @param message RSA
     * @param endMessage  end pattern of a message
     * @param img
     * @return
     */
    WritableImage getCryptedWritableImage(MessageRSA message, String endMessage, Image img) {
        ArrayList<BigInteger> codedMessage = message.getCryptedMessage();
        int paternFound = 0;
        PixelReader reader = img.getPixelReader();
        WritableImage dest = new WritableImage(reader,(int) img.getWidth(), (int) img.getHeight());
        PixelWriter writer = dest.getPixelWriter();
        PixelReader readX = dest.getPixelReader();
        int charToCode;
        for (int x = 0; x < dest.getWidth(); ++x) {
            for (int y = 0; y < dest.getHeight(); ++y) {
                Color color = reader.getColor(x, y);
                int argb = reader.getArgb(x, y);
                if ((x * dest.getWidth() + y) <= codedMessage.size() - 1) {
                    charToCode = codedMessage.get((int) (x * dest.getWidth() + y)).intValue();
                    int encodedargb = encodeColor(argb, charToCode);
                    writer.setArgb(x, y, encodedargb);
                } else if (paternFound < endMessage.length()) {
                    int codedArgb = encodeColor(argb, endMessage.charAt(paternFound));
                    writer.setArgb(x, y, codedArgb);
                    paternFound += 1;
                } else
                  writer.setColor(x, y, color);
            }
        }

        return dest;
    }

    /**
     * Decrypt the message in an image
     * @param n Private key
     * @param d Private key
     * @param endMessage end message pattern
     * @param todecrypt image to decrypt
     * @return
     */
    public MessageRSA getMessageRSA(BigInteger n, BigInteger d, String endMessage, Image todecrypt) {
        int patternFound = 0;
        PixelReader pixelReader = todecrypt.getPixelReader();
        ArrayList<BigInteger> code = new ArrayList<>();
        for (int x = 0; x < todecrypt.getWidth(); ++x) {
            for (int y = 0; y < todecrypt.getHeight(); ++y) {
                if (patternFound == endMessage.length())
                    break;

                int argb = pixelReader.getArgb(x, y);
                BigInteger decoded = decodeColor(argb);
                if ((char) decoded.intValue() == endMessage.charAt(patternFound)) {
                    patternFound += 1;
                }
                else
                    patternFound = 0;
                code.add(decoded);
            }
        }
        List<BigInteger> realMessage = code.subList(0, code.size() - endMessage.length());
        code = new ArrayList<>();
        code.addAll(realMessage);

        return new MessageRSA(code, n, d);
    }

    /**
     * Save an image into a file
     * @param image
     * @param path
     */
    public static void saveToFile(Image image,String path) {
        File outputFile = new File(path);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}