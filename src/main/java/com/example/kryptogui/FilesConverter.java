package com.example.kryptogui;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class FilesConverter {
    public static void convertToByte(File file, String format) throws IOException {
        BufferedImage readImage = ImageIO.read(file);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(readImage, format, byteStream);
        byte [] bytes = byteStream.toByteArray();
        FileOutputStream outputFile = new FileOutputStream("imageInBytes.bin");
        outputFile.write(bytes);
        outputFile.close();
    }

    public static void convertBackward(File byteFile, String format) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(byteFile);
        BufferedImage newImage = ImageIO.read(fileInputStream);
        ImageIO.write(newImage, format, new File(String.format("outputImage.%s", format)));
    }
}
