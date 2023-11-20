package com.example.kryptogui;

import org.javatuples.Triplet;

public class CustomHamming {

    public static String encode(String bytes)
    {
        int size = bytes.length();

        int[] arr = new int[size];

        for (int i = 0; i<size; i++) {
            arr[i] = Integer.parseInt(String.valueOf(bytes.charAt(i)));
        }

        int[] hammingCode = getHammingCode(arr);
        return toString(hammingCode);
    }

    private static int[] getHammingCode(int[] data) {
        int i, j = 0, k = 0;
        int size = data.length;

        int parityBits = countParityBits(size);
        int[] returnData = new int[size + parityBits];

        for(i = 0; i < returnData.length; i++) {
            if(Math.pow(2, j) == i + 1) {
                returnData[(i)] = 2;
                j++;
            }
            else {
                returnData[(k + j)] = data[k++];
            }
        }

        for(i = 0; i < parityBits; i++) {
            returnData[((int) Math.pow(2, i)) - 1] = getParityBit(returnData, i);
        }

        return returnData;
    }

    private static int countParityBits(int size) {
        int i = 0, parityBits = 0;
        while(i < size) {
            if(isParityBit(i, parityBits)) {
                parityBits++;
            }
            else {
                i++;
            }
        }
        return parityBits;
    }

    private static boolean isParityBit(int i, int parityBit) {
        return Math.pow(2, parityBit) == (i + parityBit + 1);
    }

    private static int getParityBit(int[] returnData, int pow) {
        int parityBit = 0;
        int size = returnData.length;

        for(int i = 0; i < size; i++) {
            if(returnData[i] != 2) {
                int k = (i + 1);
                String str = Integer.toBinaryString(k);

                int temp = ((Integer.parseInt(str)) / ((int) Math.pow(10, pow))) % 10;
                if(temp == 1) {
                    if(returnData[i] == 1) {
                        parityBit = (parityBit + 1) % 2;
                    }
                }
            }
        }
        return parityBit;
    }

    public static Triplet<String, String, Integer> decode(String bytes) {
        int size = bytes.length();

        int[] arr = new int[size];

        for (int i = 0; i<size; i++) {
            arr[i] = Integer.parseInt(String.valueOf(bytes.charAt(i)));
        }

        return receiveData(arr, countParityBits(arr.length));
    }

    private static Triplet<String, String, Integer> receiveData(int[] data, int parityBits) {
        int pow;
        int size = data.length;
        int[] parityArray = new int[parityBits];
        StringBuilder errorLoc = new StringBuilder();

        for(pow = 0; pow < parityBits; pow++) {
            for(int i = 0; i < size; i++) {
                int j = i + 1;
                String str = Integer.toBinaryString(j);

                int bit = ((Integer.parseInt(str)) / ((int) Math.pow(10, pow))) % 10;
                if(bit == 1) {
                    if(data[i] == 1) {
                        parityArray[pow] = (parityArray[pow] + 1) % 2;
                    }
                }
            }
            errorLoc.insert(0, parityArray[pow]);
        }


        int finalLoc = Integer.parseInt(errorLoc.toString(), 2);
        if(finalLoc != 0) {
            data[finalLoc - 1] = (data[finalLoc - 1] + 1) % 2;
        }

        int i = size - parityBits;
        int[] originalData = new int[i];
        pow = parityBits - 1;
        for(int k = size; k > 0; k--) {
            if(Math.pow(2, pow) != k) {
                i--;
                originalData[i] = data[k - 1];
            }
            else {
                pow--;
            }
        }

        return getHammingCodes(originalData, data, finalLoc);
    }

    private static Triplet<String, String, Integer> getHammingCodes(int[] originalData, int[] correctData, int errorLoc) {
        return new Triplet<>(toString(originalData), toString(correctData), errorLoc);
    }

    private static String toString(int[] ints) {
        StringBuilder result = new StringBuilder();
        for (int i : ints) {
            result.append(i);
        }
        return result.toString();
    }

}
