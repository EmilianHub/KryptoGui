package com.example.kryptogui;

import javax.crypto.Cipher;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class FileEncryptionRSA {

    public static void generateKeyPair() {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();
        CustomFileReader.saveToFile("privateKey.key", keyPair.getPrivate().getEncoded());
        CustomFileReader.saveToFile("publicKey.pub", keyPair.getPublic().getEncoded());
    }

    private static PublicKey readPublicKeyFromFile(File file) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
            X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(ks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PrivateKey readPrivateKeyFromFile(File file) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(ks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void encryptFile(File inputFileName, File publicKeyFile) {
        try {
            PublicKey publicKey = readPublicKeyFromFile(publicKeyFile);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            try (FileInputStream in = new FileInputStream(inputFileName);
                 FileOutputStream out = new FileOutputStream("encrypted.txt")) {
                processFile(cipher, in, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void decryptFile(File inputFileName, File privateKeyFile) {
        try {
            PrivateKey privateKey = readPrivateKeyFromFile(privateKeyFile);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            try (FileInputStream in = new FileInputStream(inputFileName);
                 FileOutputStream out = new FileOutputStream("decrypted.txt")) {
                processFile(cipher, in, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processFile(Cipher cipher, InputStream in, OutputStream out)
            throws javax.crypto.IllegalBlockSizeException, javax.crypto.BadPaddingException, java.io.IOException {
        byte[] inputBuffer = new byte[2048];
        int bytesRead;
        while ((bytesRead = in.read(inputBuffer)) != -1) {
            byte[] outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
            if (outputBuffer != null) out.write(outputBuffer);
        }
        byte[] outputBuffer = cipher.doFinal();
        if (outputBuffer != null) out.write(outputBuffer);
    }
}