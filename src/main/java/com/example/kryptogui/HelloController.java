package com.example.kryptogui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class HelloController {
    @FXML
    protected TextField encryptTextInput,
            decryptTextInput, keyText,
            encryptedText, decryptedText,
            encryptFileTextInput, decryptedFileText,
            encryptedFileText,
            keyFileText, decryptFileTextInput;

    @FXML
    protected ComboBox<String> menuEncryptType, menuFileEncryptType;
    HashMap<String, String> stringStringHashMap = new HashMap<>();
    File selectedKey;
    File selectedPlainMessage;
    File selectedEncryptedMessage;
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
        Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKeyFile = FileReader.readKeyFile(selectedKey);
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
        String filePath = Objects.nonNull(selectedEncryptedMessage)? selectedEncryptedMessage.getPath() : "";
        decryptFileTextInput.setText(filePath);
    }

    @FXML
    protected void onDecryptFileButton() {
        Triplet<Pair<Integer, Integer>, String, Pair<Integer, Integer>> readKeyFile = FileReader.readKeyFile(selectedKey);
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
}