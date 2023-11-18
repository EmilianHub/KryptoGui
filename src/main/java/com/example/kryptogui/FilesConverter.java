package com.example.kryptogui;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Base64;
import javax.imageio.ImageIO;

public class FilesConverter {
    public static void convertToByte(File file, String format) throws IOException {
        BufferedImage readImage = ImageIO.read(file);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(readImage, format, byteStream);
        byte [] bytes = byteStream.toByteArray();
        byte[] encode = Base64.getEncoder().encode(bytes);
        FileOutputStream outputFile = new FileOutputStream("encodedImage.txt");
        outputFile.write(encode);
        outputFile.close();
    }

    public static void convertBackward(File byteFile, String format) {
        try {
            byte[] binaryData = Base64.getDecoder().decode(Files.readAllBytes(byteFile.toPath()));
            BufferedImage newImage = ImageIO.read(new ByteArrayInputStream(binaryData));
            ImageIO.write(newImage, format, new File(String.format("outputImage.%s", format)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
