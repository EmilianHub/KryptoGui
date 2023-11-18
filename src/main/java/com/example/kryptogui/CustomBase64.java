package com.example.kryptogui;

import java.io.File;

public class CustomBase64 {
    public static String encode(String plainText) {
        byte[] bytes = plainText.getBytes();
        StringBuilder encodedText = new StringBuilder();

        for (int i = 0; i < bytes.length; i += 3) {
            int value = bytes[i] << 16;

            if (i + 1 < bytes.length) {
                value |= bytes[i + 1] << 8;
            }

            if (i + 2 < bytes.length) {
                value |= bytes[i + 2];
            }

            for (int j = 0; j < 4; j++) {
                if (i + j <= bytes.length) {
                    int index = (value >> (18 - j * 6)) & 0x3F; //0011 1111
                    encodedText.append(encodeChar(index));
                } else {
                    encodedText.append("=");
                }
            }
        }

        CustomFileReader.saveEncryptedTextString(encodedText.toString());
        return encodedText.toString();
    }

    public static String decode(String encodedText) {
        StringBuilder decodedText = new StringBuilder();
        int value = 0;
        int charCount = 0;

        for (char c : encodedText.toCharArray()) {
            if (c == '=') {
                break;
            }

            int index = decodeChar(c);
            if (index >= 0) {
                value = (value << 6) | index;
                charCount++;

                if (charCount == 4) {
                    decodedText.append((char) ((value >> 16) & 0xFF));
                    decodedText.append((char) ((value >> 8) & 0xFF));
                    decodedText.append((char) (value & 0xFF));
                    value = 0;
                    charCount = 0;
                }
            }
        }

        if (charCount == 2) {
            decodedText.append((char) ((value >> 4) & 0xFF));
        } else if (charCount == 3) {
            decodedText.append((char) ((value >> 10) & 0xFF));
            decodedText.append((char) ((value >> 2) & 0xFF));
        }

        return decodedText.toString();
    }

    private static char encodeChar(int value) {
        if (value < 26) {
            return (char) ('A' + value);
        } else if (value < 52) {
            return (char) ('a' + (value - 26));
        } else if (value < 62) {
            return (char) ('0' + (value - 52));
        } else if (value == 62) {
            return '+';
        } else {
            return '/';
        }
    }

    private static int decodeChar(char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        } else if (c >= 'a' && c <= 'z') {
            return c - 'a' + 26;
        } else if (c >= '0' && c <= '9') {
            return c - '0' + 52;
        } else if (c == '+') {
            return 62;
        } else if (c == '/') {
            return 63;
        } else {
            return -1; // Invalid character
        }
    }

    public static String encode(File plaintTextFile) {
        String plaintText = CustomFileReader.readTextFile(plaintTextFile);
        return encode(plaintText);
    }

    public static String decode(File encryptedTextFile) {
        String encryptedText = CustomFileReader.readTextFile(encryptedTextFile);
        return decode(encryptedText);
    }
}
