package com.example.kryptogui;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class CustomDES {

    public static String encrypt(String plainText, String password) {
        try {
            Cipher cipher = Cipher.getInstance("DES");

            SecretKey secretKey = generateKeyFromPassword(password);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] outputBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(outputBytes);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String decrypt(String encryptedText, String password) {
        try {
            Cipher cipher = Cipher.getInstance("DES");

            SecretKey secretKey = generateKeyFromPassword(password);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] inputBytes = Base64.getDecoder().decode(encryptedText);
            byte[] outputBytes = cipher.doFinal(inputBytes);

            return new String(outputBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static void encrypt(File plainFile, String password) throws Exception {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            SecretKey secretKey = generateKeyFromPassword(password);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] inputBytes = new FileInputStream(plainFile).readAllBytes();
            byte[] outputBytes = cipher.doFinal(inputBytes);

            FileOutputStream out = new FileOutputStream("encryptedDES.txt");
            out.write(outputBytes);
            out.flush();
    }

    public static void decrypt(File encryptedFile, String password, String extension) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

        SecretKey secretKey = generateKeyFromPassword(password);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] inputBytes = new FileInputStream(encryptedFile).readAllBytes();
        byte[] outputBytes = cipher.doFinal(inputBytes);

        FileOutputStream out = new FileOutputStream(String.format("decryptedDES.%s", extension));
        out.write(outputBytes);
        out.flush();
    }

    private static SecretKey generateKeyFromPassword(String password) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyData = sha.digest(password.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyData, 0, 8, "DES");
    }
}
