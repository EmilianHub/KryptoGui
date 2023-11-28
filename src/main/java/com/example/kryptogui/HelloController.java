package com.example.kryptogui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HelloController {
    @FXML
    protected TextField encryptTextInput,
            decryptTextInput, keyText,
            encryptedText, decryptedText,
            encryptFileTextInput, decryptedFileText,
            encryptedFileText, keyFileText, decryptFileTextInput,
            generateHamming, checkIntegral, toImageText, toBytesText,
            generatedCode, correctCode, originalCode, errorFound,
            rsaText, primeP, primeQ, rsaKeyChoose, rsaPbKeyChoose,
            rsaPvKeyChoose, rsaTextFileChoose;

    @FXML
    protected ComboBox<String> menuEncryptType, menuFileEncryptType, extensionBox;

    @FXML
    protected TextArea rsaSubmit;

    HashMap<String, String> stringStringHashMap = new HashMap<>();
    File selectedKey, selectedPlainMessage, selectedEncryptedMessage, toBytesImage, toImageBytes, rsaKey,
            publicKeyFile, privateKeyFile, rsaFile;
    FileChooser fileChooser = new FileChooser();

    public void initialize() {
        stringStringHashMap.put("Transpozycja spiralna", "spiral");
        stringStringHashMap.put("Transpozycja diagonalna", "diagonal");
        stringStringHashMap.put("Transpozycja kwadratowa", "square");
        stringStringHashMap.put("Monoalfabet", "monoalfabet");
        stringStringHashMap.put("Polialfabet", "polialfabet");
        stringStringHashMap.put("Base64", "base64");
        ObservableList<String> encryptTypeList = FXCollections.observableArrayList(stringStringHashMap.keySet().stream().toList());
        menuEncryptType.setItems(encryptTypeList);
        menuFileEncryptType.setItems(encryptTypeList);

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(
                        "All Files",
                        "*.*"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/desktop"));

        ObservableList<String> extensionTypes = FXCollections.observableArrayList("jpg", "png");
        extensionBox.setItems(extensionTypes);

        primeQ.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                primeQ.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        primeP.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                primeP.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    protected void onEncryptButton() {
        String type = onTypeMenu();
        String message = encryptTextInput.getText();
        encryptWithType(type, message);
    }

    private void encryptWithType(String type, String message) {
        String encryptText;
        Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> generatedKey = KeyGenerator.generateKey(type, message);
        switch (type) {
            case "base64" -> encryptText = CustomBase64.encode(message);
            default -> {
                TransPol transPol = new TransPol(message, generatedKey);
                encryptText = transPol.encrypt(type);
            }
        }
        encryptedText.setText(encryptText);
    }

    @FXML
    protected String onTypeMenu() {
        return stringStringHashMap.get(menuEncryptType.getValue());
    }

    @FXML
    protected String onTypeMenuFile() {
        return stringStringHashMap.get(menuFileEncryptType.getValue());
    }

    @FXML
    protected void onDecryptButton() {
        Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKeyFile = CustomFileReader.readKeyFile(selectedKey);
        String message = decryptTextInput.getText();
        assert readKeyFile != null;
        decryptWithType(readKeyFile, message);
    }

    private void decryptWithType(Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKeyFile, String message) {
        String type = readKeyFile.getValue1();
        String decryptText;
        switch (type) {
            case "base64" -> decryptText = CustomBase64.decode(message);
            default -> {
                TransPol transPol = new TransPol(message, readKeyFile);
                decryptText = transPol.decryptWithInput(message, readKeyFile);
            }
        }
        decryptedText.setText(decryptText);
    }

    @FXML
    protected void onKeyChoose() {
        selectedKey = fileChooser.showOpenDialog(new Stage());
        String filePath = Objects.nonNull(selectedKey) ? selectedKey.getPath() : "";
        keyText.setText(filePath);
        keyFileText.setText(filePath);
    }

    @FXML
    protected void onEncryptButtonFile() {
        String type = onTypeMenuFile();
        encryptWithType(type);
    }

    private void encryptWithType(String type) {
        Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> key = KeyGenerator.generateKey(type, selectedPlainMessage);
        String encryptText;
        switch (type) {
            case "base64" -> encryptText = CustomBase64.encode(selectedPlainMessage);
            default -> {
                TransPol transPol = new TransPol(selectedPlainMessage, key);
                encryptText = transPol.encrypt(type);
            }
        }
        encryptedFileText.setText(encryptText);
    }

    @FXML
    protected void onChoosePlainMessage() {
        selectedPlainMessage = fileChooser.showOpenDialog(new Stage());
        String filePath = Objects.nonNull(selectedPlainMessage) ? selectedPlainMessage.getPath() : "";
        encryptFileTextInput.setText(filePath);
    }

    @FXML
    protected void onEncryptedMessageChoose() {
        selectedEncryptedMessage = fileChooser.showOpenDialog(new Stage());
        String filePath = Objects.nonNull(selectedEncryptedMessage) ? selectedEncryptedMessage.getPath() : "";
        decryptFileTextInput.setText(filePath);
    }

    @FXML
    protected void onDecryptFileButton() {
        Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKeyFile = CustomFileReader.readKeyFile(selectedKey);
        assert readKeyFile != null;
        decryptWithType(readKeyFile, selectedEncryptedMessage);
    }

    private void decryptWithType(Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKeyFile, File encryptedFile) {
        String type = readKeyFile.getValue1();
        String decryptText;
        switch (type) {
            case "base64" -> decryptText = CustomBase64.decode(encryptedFile);
            default -> {
                TransPol transPol = new TransPol(encryptedFile, readKeyFile);
                decryptText = transPol.decryptWithFile(encryptedFile, readKeyFile);
            }
        }
        decryptedFileText.setText(decryptText);
    }

    @FXML
    protected void onToBytes() {
        toBytesImage = fileChooser.showOpenDialog(new Stage());
        String filePath = Objects.nonNull(toBytesImage) ? toBytesImage.getPath() : "";
        toBytesText.setText(filePath);
    }

    @FXML
    protected void onToBytesButton() throws IOException {
        FilesConverter.convertToByte(toBytesImage, onExtensionBox());
    }

    @FXML
    protected void onToImage() {
        toImageBytes = fileChooser.showOpenDialog(new Stage());
        String filePath = Objects.nonNull(toImageBytes) ? toImageBytes.getPath() : "";
        toImageText.setText(filePath);
    }

    @FXML
    protected void onRollbackButton() {
        FilesConverter.convertBackward(toImageBytes, onExtensionBox());
    }

    @FXML
    protected String onExtensionBox() {
        return extensionBox.getValue();
    }

    @FXML
    protected void onGenerateButton() {
        String bytes = generateHamming.getText();
        String encode = CustomHamming.encode(bytes);
        generatedCode.setText(encode);
    }

    @FXML
    protected void onCheckButton() {
        String bytes = checkIntegral.getText();
        Triplet<String, String, Integer> decode = CustomHamming.decode(bytes);
        correctCode.setText(decode.getValue1());
        originalCode.setText(decode.getValue0());
        errorFound.setText(decode.getValue2().toString());
    }

    @FXML
    protected void onRsaEncrypt() {
        try {
            String text = rsaText.getText();
            int p = Integer.parseInt(primeP.getText());
            int q = Integer.parseInt(primeQ.getText());
            RSA rsa = new RSA(p, q);
            List<BigInteger> encrypt = rsa.encrypt(text);
            setRsaResult(encrypt.toString(), rsa, false);
        } catch (Exception e) {
            showAlert(e.getMessage());
        }
    }

    @FXML
    protected void onRsaDecrypt() {
        try {
            String text = rsaText.getText();
            RSA rsa = new RSA(rsaKey);
            String decrypt = rsa.decrypt(text);
            setRsaResult(decrypt, rsa, true);
        } catch (Exception e) {
            showAlert(e.getMessage());
        }
    }

    private void showAlert(String e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Podane liczby nie są względnie pierwsze", ButtonType.OK);
        alert.showAndWait();
    }

    public void setRsaResult(String result, RSA rsa, boolean isDecrypted) {
        String text = String.format("N = %s\nTocjent = %s\nWynik: %s", rsa.getN(), rsa.getPhi(), result);
        if(isDecrypted) {
            text = String.format("N = %s\nKlucz prywatny = %s\nWynik: %s", rsa.getN(), rsa.getD(), result);
        }
        rsaSubmit.setText(text);
    }

    @FXML
    protected void onRsaKeyChoose() {
        rsaKey = fileChooser.showOpenDialog(new Stage());
        String filePath = Objects.nonNull(rsaKey) ? rsaKey.getPath() : "";
        rsaKeyChoose.setText(filePath);
    }

    @FXML
    protected void onKeyGenerate() {
        FileEncryptionRSA.generateKeyPair();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Klucze zostały wygenerowane", ButtonType.OK);
        alert.showAndWait();
    }

    @FXML
    protected void onRsaTextFileChoose() {
        rsaFile = fileChooser.showOpenDialog(new Stage());
        String path = Objects.nonNull(rsaFile) ? rsaFile.getPath() : "";
        rsaTextFileChoose.setText(path);
    }

    @FXML
    protected void onRsaPbKeyChoose() {
        publicKeyFile = fileChooser.showOpenDialog(new Stage());
        String path = Objects.nonNull(publicKeyFile) ? publicKeyFile.getPath() : "";
        rsaPbKeyChoose.setText(path);
    }

    @FXML
    protected void onRsaPvKeyChoose() {
        privateKeyFile = fileChooser.showOpenDialog(new Stage());
        String path = Objects.nonNull(privateKeyFile) ? privateKeyFile.getPath() : "";
        rsaPvKeyChoose.setText(path);
    }

    @FXML
    protected void onEnRsaFile() {
        FileEncryptionRSA.encryptFile(rsaFile, publicKeyFile);
    }

    @FXML
    protected void onDeRsaFile() {
        FileEncryptionRSA.decryptFile(rsaFile, privateKeyFile);
    }
}