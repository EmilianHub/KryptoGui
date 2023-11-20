package com.example.kryptogui;

import org.javatuples.Triplet;
import org.javatuples.Pair;

import java.io.File;
import java.util.*;

public class TransPol {

    private static final Random random = new Random();
    private String password;
    private char[] passwordAsCharArray;
    private Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> key;
    int repeat;
    int i;
    int p;
    int widthPoint;
    int heightPoint;
    int decodedLetters;
    int foundLetters;

    TransPol(String password, Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> generatedKey) {
        initKey(generatedKey);
        init(password);
    }

    private void initKey(Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> generatedKey) {
        this.key = generatedKey;
        this.widthPoint = generatedKey.getValue2().getValue1();
        this.heightPoint = generatedKey.getValue2().getValue0();
    }

    TransPol(File file, Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> generatedKey) {
        String password = CustomFileReader.readTextFile(file);
        initKey(generatedKey);
        init(password);
    }

    private void init(String password) {
        if (!password.isBlank()) {
            this.password = password;
            this.repeat = 1;
            this.i = 0;
            this.p = 0;
        } else {
            throw new RuntimeException("Password has not been initialized");
        }
    }

    private Pair<Integer, Integer> getSize() {
        return key.getValue0();
    }

    private Pair<Integer, Integer> getStartPoint() {
        return key.getValue2();
    }

    private int roundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }

    public String encrypt(String type) {
        passwordAsCharArray = password.toCharArray();
        Character[][] encrypt = new Character[0][0];
        String encryptString = "";

        switch (type) {
            case "spiral" -> encrypt = encryptAsSpiral();
            case "diagonal" -> encrypt = encryptAsDiagonal();
            case "square" -> encrypt = encryptAsSquare();
            case "monoalfabet" -> {
                int key = getShiftKey(getSize());
                encryptString = shiftEncryptMono(password, key);
            }
            case "polialfabet" -> encryptString = shiftEncryptionPoli(password, type);
        }
        if (!encryptString.isBlank()) {
            CustomFileReader.saveEncryptedTextString(encryptString);
            return encryptString;
        } else {
            CustomFileReader.saveEncryptedText(encrypt);
            return chararrayToString(encrypt);
        }
    }

    public String shiftEncryptionPoli(String plaintext, String key) {
        StringBuilder ciphertext = new StringBuilder();
        int keyLength = key.length();

        for (int i = 0; i < plaintext.length(); i++) {
            char plainChar = plaintext.charAt(i);

            if (Character.isLetter(plainChar)) {
                char keyChar = key.charAt(i % keyLength);
                int shift = Character.toUpperCase(keyChar) - 'A';
                char encryptedChar = encryptChar(plainChar, shift);
                ciphertext.append(encryptedChar);
            } else {
                ciphertext.append(plainChar);
            }
        }

        return ciphertext.toString();
    }

    public String shiftDecryptionPoli(String ciphertext, String key) {
        StringBuilder plaintext = new StringBuilder();
        int keyLength = key.length();

        for (int i = 0; i < ciphertext.length(); i++) {
            char cipherChar = ciphertext.charAt(i);

            if (Character.isLetter(cipherChar)) {
                char keyChar = key.charAt(i % keyLength);
                int shift = Character.toUpperCase(keyChar) - 'A';
                char decryptedChar = decryptChar(cipherChar, shift);
                plaintext.append(decryptedChar);
            } else {
                plaintext.append(cipherChar);
            }
        }

        return plaintext.toString();
    }

    public static char encryptChar(char c, int shift) {
        if (Character.isUpperCase(c)) {
            return (char) ((c - 'A' + shift) % 26 + 'A');
        } else {
            return (char) ((c - 'a' + shift) % 26 + 'a');
        }
    }

    public static char decryptChar(char c, int shift) {
        shift = (26 - shift) % 26;
        if (Character.isUpperCase(c)) {
            return (char) ((c - 'A' + shift) % 26 + 'A');
        } else {
            return (char) ((c - 'a' + shift) % 26 + 'a');
        }
    }

    private String chararrayToString(Character[][] encrypt) {
        StringBuilder result = new StringBuilder();
        for (Character[] characters : encrypt) {
            for (Character character : characters) {
                char text = character == null ? '-' : character;
                result.append(text);
            }
            result.append("$");
        }
        return result.toString();
    }

    private Character[][] encryptAsSpiral() {
        Pair<Integer, Integer> size = getSize();
        Character[][] shape = createShape(size);
        boolean encrypted = false;

        shape[heightPoint][widthPoint] = passwordAsCharArray[0];
        List<String> route = findRoute(size, getStartPoint());
        while (!encrypted) {
            for (String s : route) {
                encrypted = nextMove(shape, s, i++);
            }
        }
        return shape;
    }

    private Character[][] createShape(Pair<Integer, Integer> size) {
        return new Character[size.getValue0()][size.getValue1()];
    }

    private boolean nextMove(Character[][] shape, String route, Integer i) {
        if (i != 0 && Math.floorMod(i, 2) == 0) {
            repeat++;
        }
        switch (route) {
            case "r" -> {
                for (int l = 0; l < repeat; l++) {
                    if (isOutOfBound()) {
                        return true;
                    }
                    widthPoint = Math.min(widthPoint + 1, shape[1].length - 1);
                    shape[heightPoint][widthPoint] = passwordAsCharArray[p];
                }
            }
            case "l" -> {
                for (int l = 0; l < repeat; l++) {
                    if (isOutOfBound()) {
                        return true;
                    }
                    widthPoint = Math.max(widthPoint - 1, 0);
                    shape[heightPoint][widthPoint] = passwordAsCharArray[p];
                }
            }
            case "d" -> {
                for (int l = 0; l < repeat; l++) {
                    if (isOutOfBound()) {
                        return true;
                    }
                    heightPoint = Math.min(heightPoint + 1, shape.length - 1);
                    shape[heightPoint][widthPoint] = passwordAsCharArray[p];
                }
            }
            case "g" -> {
                for (int l = 0; l < repeat; l++) {
                    if (isOutOfBound()) {
                        return true;
                    }
                    heightPoint = Math.max(heightPoint - 1, 0);
                    shape[heightPoint][widthPoint] = passwordAsCharArray[p];
                }
            }
        }
        return false;
    }

    private boolean isOutOfBound() {
        p++;
        return p >= passwordAsCharArray.length;
    }

    private Character[][] encryptAsDiagonal() {
        Pair<Integer, Integer> size = getSize();
        Character[][] shape = createShape(size);

        toDiagonal(shape);
        return shape;
    }

    private Character[][] encryptAsSquare() {
        Pair<Integer, Integer> size = getSize();
        Character[][] shape = createShape(size);

        toSquare(shape);
        return shape;
    }

    private void toSquare(Character[][] shape) {
        for (int width = 0; width < shape[1].length; width++) {
            for (int height = 0; height < shape.length; height++) {
                shape[height][width] = passwordAsCharArray[p];
                if (isOutOfBound()) {
                    return;
                }
            }
        }
    }


    private void toDiagonal(Character[][] shape) {
        for (int width = shape[1].length - 1; width >= 0; width--) {
            for (int height = 0; height <= i; height++) {
                shape[height][width + height] = passwordAsCharArray[p];
                if (isOutOfBound()) {
                    return;
                }
            }
            i++;
        }
        if (p != passwordAsCharArray.length) {
            for (int height = 1; height < shape.length - 1; height++) {
                for (int width = 0; width < i - 1; width++) {
                    shape[height + width][width] = passwordAsCharArray[p];
                    if (isOutOfBound()) {
                        return;
                    }
                }
                i--;
            }
        }
    }

    private List<String> findRoute(Pair<Integer, Integer> size, Pair<Integer, Integer> startPoint) {
        List<String> order = new ArrayList<>();
        if (size.getValue0() - startPoint.getValue0() >= startPoint.getValue0()) {
            order.add("r");
            order.add("l");
        } else {
            order.add("l");
            order.add("r");
        }
        if (size.getValue1() - startPoint.getValue1() >= startPoint.getValue1()) {
            order.add(1, "d");
            order.add("g");
        } else {
            order.add(1, "g");
            order.add("d");
        }

        return order;
    }

    public String decryptWithInput(String message, Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKey) {
        return decodeMessageBasedOnKey(message, readKey);
    }

    public String decryptWithFile(File encryptedFile, Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKey) {
        String message = CustomFileReader.readTextFile(encryptedFile);
        return decodeMessageBasedOnKey(message, readKey);
    }

    private String decodeMessageBasedOnKey(String data, Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKey) {
        String[] strings = data.split("\\$");
        i = 0;
        String decryptedMessage = "";
        switch (readKey.getValue1()) {
            case "spiral" -> decryptedMessage = decodeSpiral(readKey, strings);
            case "diagonal" -> decryptedMessage = decodeDiagonal(readKey, strings);
            case "square" -> decryptedMessage = decodeSquare(readKey, strings);
            case "monoalfabet" -> {
                int key = getShiftKey(readKey.getValue0());
                decryptedMessage = shiftDecryptMono(data, key);
            }
            case "polialfabet" -> decryptedMessage = shiftDecryptionPoli(data, readKey.getValue1());
        }
        return decryptedMessage;
    }

    private int getShiftKey(Pair<Integer, Integer> size) {
        return roundUp(size.getValue0() + size.getValue1(), 2);
    }

    private String decodeSquare(Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKey, String[] strings) {
        Pair<Integer, Integer> size = readKey.getValue0();
        StringBuilder decodedTextBuilder = new StringBuilder();

        for (int width = 0; width < size.getValue1(); width++) {
            for (int height = 0; height < size.getValue0(); height++) {
                char[] charArray = strings[height].toCharArray();
                char c = charArray[width];
                isNotStopCharacter(c, decodedTextBuilder);
            }
        }
        return decodedTextBuilder.toString();
    }

    private String decodeDiagonal(Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKey, String[] strings) {
        Pair<Integer, Integer> size = readKey.getValue0();
        StringBuilder decodedTextBuilder = new StringBuilder();
        countLetters(strings);

        for (int width = size.getValue1() - 1; width >= 0; width--) {
            for (int height = 0; height <= i; height++) {
                char[] charArray = strings[height].toCharArray();
                char c = charArray[width + height];
                isNotStopCharacter(c, decodedTextBuilder);
            }
            i++;
        }
        if (decodedLetters != foundLetters) {
            for (int height = 1; height < size.getValue0() - 1; height++) {
                for (int width = 0; width < i - 1; width++) {
                    char[] charArray = strings[width + height].toCharArray();
                    char c = charArray[width];
                    isNotStopCharacter(c, decodedTextBuilder);
                }
                i--;
            }
        }
        return decodedTextBuilder.toString();
    }

    private String decodeSpiral(Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKey, String[] strings) {
        Pair<Integer, Integer> size = readKey.getValue0();
        Pair<Integer, Integer> startPoints = readKey.getValue2();
        widthPoint = startPoints.getValue1();
        heightPoint = startPoints.getValue0();
        p = 0;
        repeat = 1;
        decodedLetters = 1;
        boolean encrypted = false;
        List<String> route = findRoute(size, startPoints);
        countLetters(strings);
        StringBuilder decodedTextBuilder = new StringBuilder();

        char[] charArray = strings[heightPoint].toCharArray();
        char c = charArray[widthPoint];
        decodedTextBuilder.append(c);

        while (!encrypted) {
            for (String s : route) {
                encrypted = nextMove(strings, s, i++, decodedTextBuilder);
                if (encrypted) break;
            }
        }
        return decodedTextBuilder.toString();
    }

    private void countLetters(String[] strings) {
        int count = 0;
        for (String string : strings) {
            for (char letter : string.toCharArray()) {
                if (letter != '-') count++;
            }
        }
        foundLetters = count;
    }

    private boolean nextMove(String[] strings, String route, Integer i, StringBuilder decodedTextBuilder) {
        if (i != 0 && Math.floorMod(i, 2) == 0) {
            repeat++;
        }
        switch (route) {
            case "r" -> {
                for (int l = 0; l < repeat; l++) {
                    char[] row = strings[heightPoint].toCharArray();
                    widthPoint = Math.min(widthPoint + 1, row.length - 1);
                    char character = row[widthPoint];
                    isNotStopCharacter(character, decodedTextBuilder);
                    if (decodedLetters == foundLetters) {
                        return true;
                    }
                    p++;
                }
            }
            case "l" -> {
                for (int l = 0; l < repeat; l++) {
                    char[] row = strings[heightPoint].toCharArray();
                    widthPoint = Math.max(widthPoint - 1, 0);
                    char character = row[widthPoint];
                    isNotStopCharacter(character, decodedTextBuilder);
                    if (decodedLetters == foundLetters) {
                        return true;
                    }
                    p++;
                }
            }
            case "d" -> {
                for (int l = 0; l < repeat; l++) {
                    heightPoint = Math.min(heightPoint + 1, strings.length - 1);
                    char[] row = strings[heightPoint].toCharArray();
                    char character = row[widthPoint];
                    isNotStopCharacter(character, decodedTextBuilder);
                    if (decodedLetters == foundLetters) {
                        return true;
                    }
                    p++;
                }
            }
            case "g" -> {
                for (int l = 0; l < repeat; l++) {
                    heightPoint = Math.max(heightPoint - 1, 0);
                    char[] row = strings[heightPoint].toCharArray();
                    char character = row[widthPoint];
                    isNotStopCharacter(character, decodedTextBuilder);
                    if (decodedLetters == foundLetters) {
                        return true;
                    }
                    p++;
                }
            }
        }
        return false;
    }

    private void isNotStopCharacter(char character, StringBuilder decodedTextBuilder) {
        if ('-' != character) {
            decodedTextBuilder.append(character);
            decodedLetters++;
        }
    }

    public String shiftEncryptMono(String text, int key) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);

            if (Character.isLetter(character)) {
                char base = Character.isUpperCase(character) ? 'A' : 'a';
                character = (char) ((character - base + key) % 26 + base);
                if (character < base) {
                    character += 26;
                }
            }

            result.append(character);
        }

        return result.toString();
    }

    public String shiftDecryptMono(String text, int key) {
        key = (26 - key) % 26;
        return shiftEncryptMono(text, key);
    }
}
