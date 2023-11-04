package com.example.kryptogui;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class FileReader {
    public static String readTextFile(File file) {
        StringBuilder result = new StringBuilder();
        try {
            Scanner myReader = new Scanner(file);
            if (myReader.hasNextLine()) {
                result.append(myReader.nextLine());
            }
            myReader.close();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't read file");
        }
        return result.toString();
    }

    public static Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKeyFile(File file) {
        try {
            Scanner myReader = new Scanner(file);
            if (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] split = data.split(";");
                return createReadKey(split);
            }
            myReader.close();
        } catch (Exception e) {
            throw new RuntimeException("Couldn't read key");
        }
        return null;
    }

    private static Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> createReadKey(String[] split) {
        Pair<Integer, Integer> size = new Pair<>(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
        String method = split[2];
        Pair<Integer, Integer> startPoints = new Pair<>(Integer.valueOf(split[3]), Integer.valueOf(split[4]));
        return new Triplet<>(size, method, startPoints);
    }

    public static void saveKeyToFile(String key) {
        try {
            File file = new File("key.txt");
            if (!file.exists()) file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(key);
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveEncryptedTextString(String encryptedText) {
        try {
            File file = new File("message.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (Character character : encryptedText.toCharArray()) {
                bufferedWriter.write(character);
            }
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveEncryptedText(Character[][] encryptedText) {
        try {
            File file = new File("message.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (Character[] characters : encryptedText) {
                for (Character character : characters) {
                    char text = character == null ? '-' : character;
                    bufferedWriter.write(text);
                }
                bufferedWriter.write("$");
            }
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveBytesToFile(byte[] bytes) {
        try {
            File file = new File("image.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (byte pByte: bytes) {
                bufferedWriter.write(pByte);
            }
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
