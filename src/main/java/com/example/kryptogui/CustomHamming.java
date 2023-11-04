package com.example.kryptogui;

public class CustomHamming {

    public static String encodeHamming(String input) {
        int dataLength = input.length();
        int parityBits = 4;

        StringBuilder encodedData = new StringBuilder(dataLength + parityBits);

        for (int i = 0; i < parityBits; i++) {
            encodedData.append('0');
        }

        for (int i = 0, j = 0; i < dataLength + parityBits; i++) {
            if (i == (1 << j) - 1) {
                j++;
            } else {
                encodedData.append(input.charAt(i - j));
            }
        }

        for (int j = 0; j < parityBits; j++) {
            int parityBitPosition = (1 << j) - 1;
            int parity = calculateParity(encodedData.toString(), parityBitPosition);
            encodedData.setCharAt(parityBitPosition, (parity == 1) ? '1' : '0');
        }

        return encodedData.toString();
    }

    public static int calculateParity(String data, int parityBitPosition) {
        int parity = 0;
        for (int i = parityBitPosition; i < data.length(); i += (parityBitPosition + 1) * 2) {
            for (int j = i; j < Math.min(i + parityBitPosition + 1, data.length()); j++) {
                if (data.charAt(j) == '1') {
                    parity ^= 1;
                }
            }
        }
        return parity;
    }

    public static String decodeHamming(String receivedData) {
        int parityBits = 4;
        StringBuilder decodedData = new StringBuilder(receivedData.length() - parityBits);

        for (int i = 0, j = 0; i < receivedData.length(); i++) {
            if (i == (1 << j) - 1) {
                j++;
            } else {
                decodedData.append(receivedData.charAt(i));
            }
        }

        int errorPosition = 0;
        for (int j = 0; j < parityBits; j++) {
            int parityBitPosition = (1 << j) - 1;
            int parity = calculateParity(receivedData, parityBitPosition);
            if (parity != 0) {
                errorPosition += parityBitPosition + 1;
            }
        }

        if (errorPosition > 0) {
            decodedData.setCharAt(errorPosition - 1, (decodedData.charAt(errorPosition - 1) == '1') ? '0' : '1');
        }

        return decodedData.toString();
    }

}
