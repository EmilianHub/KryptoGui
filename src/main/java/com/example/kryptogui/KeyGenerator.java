package com.example.kryptogui;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.File;
import java.util.Random;

public class KeyGenerator {

    public static Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> generateKey(String method, String password) {
        int upperBound = Math.max(roundUp(password.length(), 3), 5);
        int lowerBound = upperBound - 1;

        int length = generateLength(lowerBound, upperBound);
        ///Miało być jeszcze losowanie figury (kwadrat/prostokąt) ale zabrakło czasu
        Pair<Integer, Integer> size = new Pair<>(length, length);
        Pair<Integer, Integer> startPoint = findStartPoint(method, length, length);
        String keyAsString = String.format("%s;%s;%s;%s;%s", length, length, method, startPoint.getValue0(), startPoint.getValue1());
        FileReader.saveKeyToFile(keyAsString);

        return new Triplet<>(size, method, startPoint);
    }

    private static int roundUp(int num, int divisor) {
        return (num + divisor - 1) / divisor;
    }

    private static Pair<Integer, Integer> findStartPoint(String method, int height, int width) {
        int heightPoint;
        int widthPoint;
        if ("spiral".equalsIgnoreCase(method)) {
            widthPoint = Math.floorDiv(width - 1, 2);
            heightPoint = Math.floorDiv(height - 1, 2);
        } else {
            widthPoint = width;
            heightPoint = 0;
        }
        return new Pair<>(heightPoint, widthPoint);
    }

    private static int generateLength(int min, int max) {
        return new Random().ints(min, max + 1).findFirst().getAsInt();
    }

    public static Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> generateKey(String type, File selectedPlainMessage) {
        String plainText = FileReader.readTextFile(selectedPlainMessage);
        return generateKey(type, plainText);
    }
}
