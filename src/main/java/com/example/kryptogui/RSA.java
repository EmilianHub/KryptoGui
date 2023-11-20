package com.example.kryptogui;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RSA {

    private BigInteger n, d, e, phi;

    public RSA(int p, int q) {
        BigInteger bigP = BigInteger.valueOf(p);
        BigInteger bigQ = BigInteger.valueOf(q);
        if (!bigQ.isProbablePrime(100) || !bigP.isProbablePrime(100)) {
            throw new RuntimeException("Podane liczby nie są pierwsze");
        }
        n = bigP.multiply(bigQ);

        phi = bigP.subtract(BigInteger.ONE).multiply(bigQ.subtract(BigInteger.ONE));

        e = new BigInteger(phi.getLowestSetBit(), new Random());
        while (phi.gcd(e).intValue() > 1) {
            e = e.add(new BigInteger("2"));
        }

        d = e.modInverse(phi);
        CustomFileReader.saveKeyToFile(String.format("%s, %s", d, n));
    }

    public List<BigInteger> encrypt(String message) {
        List<BigInteger> result = new ArrayList<>();
        byte[] messageBytes = message.getBytes();
        for (byte pChar : messageBytes) {
            BigInteger messageInt = new BigInteger(String.valueOf(pChar));
            result.add(messageInt.modPow(e, n));
        }

        return result;
    }

    public RSA(File keyFile) {
        String privateKey = CustomFileReader.readTextFile(keyFile);
        String[] split = privateKey.split(",");
        d = new BigInteger(split[0].trim());
        n = new BigInteger(split[1].trim());
    }

    public String decrypt(String encryptedMessage) {
        encryptedMessage = encryptedMessage.replaceAll("[\\[\\] ]", "");
        String[] split = encryptedMessage.split(",");
        StringBuilder result = new StringBuilder();
        for (String bigIntAsString : split) {
            BigInteger pBigInt = new BigInteger(bigIntAsString);
            BigInteger decryptedMessageInt = pBigInt.modPow(d, n);
            byte[] decryptedMessageBytes = decryptedMessageInt.toByteArray();
            String pChar = new String(decryptedMessageBytes);
            result.append(pChar);
        }
        return result.toString();
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getPhi() {
        return phi;
    }

    public BigInteger getD() {
        return d;
    }
}
