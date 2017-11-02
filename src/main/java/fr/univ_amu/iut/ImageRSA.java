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

    public static int[] getIntARGB(int argb) {
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

    ImageView getCryptedImageView(MessageRSA message, String endMessage, Image img) {
        WritableImage dest = getCryptedWritableImage(message, endMessage, img);
        return new ImageView(dest);
    }

    WritableImage getCryptedWritableImage(MessageRSA message, String endMessage, Image img) {
        ArrayList<BigInteger> codedMessage = message.getCryptedMessage();
        int paternFound = 0;
        PixelReader reader = img.getPixelReader();
        WritableImage dest = new WritableImage((int) img.getWidth(), (int) img.getHeight());
        PixelWriter writer = dest.getPixelWriter();
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
                    writer.setArgb(x, y, encodeColor(argb, endMessage.charAt(paternFound)));
                    paternFound += 1;
                } else
                  writer.setColor(x, y, color);
            }
        }

        return dest;
    }

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