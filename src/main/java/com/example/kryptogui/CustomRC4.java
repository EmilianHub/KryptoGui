package com.example.kryptogui;

public class CustomRC4 {
    public static String encrypt(String plaintext, String key) {
        int[] numbers = new int[256];
        int[] keyChars = new int[256];

        for (int i = 0; i < 256; i++) {
            numbers[i] = i;
            keyChars[i] = key.charAt(i % key.length());
        }

        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + numbers[i] + keyChars[i]) % 256;
            int temp = numbers[i];
            numbers[i] = numbers[j];
            numbers[j] = temp;
        }

        int i = 0;
        j = 0;
        StringBuilder result = new StringBuilder();

        for (char ch : plaintext.toCharArray()) {
            i = (i + 1) % 256;
            j = (j + numbers[i]) % 256;

            int temp = numbers[i];
            numbers[i] = numbers[j];
            numbers[j] = temp;

            int K = numbers[(numbers[i] + numbers[j]) % 256];

            result.append((char) (ch ^ K));
        }

        return result.toString();
    }

    public static String decrypt(String encryptedText, String key) {
        return encrypt(encryptedText, key);
    }
}
