package com.example.kryptogui;

import java.io.File;

public class CustomRC4 {
    public static String encrypt(String plaintext, String key) {
        int[] S = new int[256];
        int[] T = new int[256];

        for (int i = 0; i < 256; i++) {
            S[i] = i;
            T[i] = key.charAt(i % key.length());
        }

        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + S[i] + T[i]) % 256;
            int temp = S[i];
            S[i] = S[j];
            S[j] = temp;
        }

        int i = 0;
        j = 0;
        StringBuilder cipher = new StringBuilder();

        for (char ch : plaintext.toCharArray()) {
            i = (i + 1) % 256;
            j = (j + S[i]) % 256;
            // Zamień S[i] i S[j]
            int temp = S[i];
            S[i] = S[j];
            S[j] = temp;

            int K = S[(S[i] + S[j]) % 256];
            // Operacja XOR na poziomie bajtów
            cipher.append((char) (ch ^ K));
        }

        return cipher.toString();
    }

    public static String decrypt(String encryptedText, String key) {
        return encrypt(encryptedText, key);
    }
}
