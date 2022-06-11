package com.example.phonebook;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ChoiceBox<String> phonebookPicker;
    @FXML
    private ComboBox<String> myComboBox;
    @FXML
    private TextField newNameField, nameField, lnameField, phoneField, streetField, cityField, postField, textFieldSearch,
            nameNewField, lnameNewField, phoneNewField, streetNewField, cityNewField, postNewField;
    @FXML
    private Label bookName;
    @FXML
    private Button addButton, editButton, deleteButton, createButton, cancelButton, backButton, addRecord,
            searchEditDelButton, searchRecordBtn, deleteRecordBtn, editRecordBtn, showAllBtn;
    @FXML RadioButton rbName, rbLast, rbPhone, rbCity, rbPost;
    private Stage stage;
    private Parent root;
    public static String currentTable = "";
    public static String searchCriteria = "";
    public static String selectedRecordInfo[] = {"", "", "", "", "", "", ""};
    public static URL myUrl;
    ArrayList<String> phonebookList = new ArrayList<String>();
    @FXML
    private TableView<ContactInformation> table;
    @FXML
    private TableColumn<ContactInformation, String> firstnameCol;
    @FXML
    private TableColumn<ContactInformation, String> lastnameCol;
    @FXML
    private TableColumn<ContactInformation, String> phoneCol;
    @FXML
    private TableColumn<ContactInformation, String> streetCol;
    @FXML
    private TableColumn<ContactInformation, String> cityCol;
    @FXML
    private TableColumn<ContactInformation, String> postCol;
    @FXML
    private TableColumn<ContactInformation, Integer> idCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        myUrl = url;
        if(phonebookPicker != null) {
            phonebookList = JDBCquery.loadPhonebookList();
            if (!phonebookList.isEmpty()) {
                for (int i = 0; i < phonebookList.size(); i++) {
                    phonebookPicker.getItems().add(phonebookList.get(i));
                }
            }
        }
        if(nameNewField != null) {
            nameNewField.setText("");
            lnameNewField.setText("");
            phoneNewField.setText("");
            streetNewField.setText("");
            cityNewField.setText("");
            postNewField.setText("");
        }

        if (nameField != null) {
            lnameField.setText(selectedRecordInfo[1]);
            nameField.setText(selectedRecordInfo[0]);
            phoneField.setText(selectedRecordInfo[2]);
            streetField.setText(selectedRecordInfo[3]);
            cityField.setText(selectedRecordInfo[4]);
            postField.setText(selectedRecordInfo[5]);
        }
    }
    public void addNewTableButtonClick(ActionEvent event) throws IOException {
        changeScene("sceneCreateTable.fxml", event);
    }
    public void changeScene(String sceneName, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneName));
        root = loader.load();
        Scene scene = new Scene(root);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    public void cancelButtonClick(ActionEvent event) throws IOException {
        changeScene("sceneStart.fxml", event);
    }
    public void createButtonClick(ActionEvent event) throws IOException {
        boolean exists = false;
        boolean proper = false;
        if(!(newNameField.getText().isEmpty())) {
            String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            for (int i = 0; i<letters.length(); i++) {

                if (newNameField.getText().charAt(0) == (letters.charAt(i))) {
                    proper = true;
                }
            }
            System.out.println(newNameField.getText().charAt(0));
            if(proper) {
                exists = JDBCquery.createNewPhonebook(newNameField.getText());
                newNameField.setText("");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Phonebook created sucessfully!");
                alert.setHeaderText("Phonebook created sucessfully!");
                alert.show();
                if(!exists) {
                    changeScene("sceneStart.fxml", event);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Improper name!");
                alert.setHeaderText("The name is not proper.");
                alert.setContentText("The name should start with a letter of alphabet.");
                alert.show();
                newNameField.setText("");
            }
        }
        if(exists) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Table already exists!");
            alert.setContentText("Try using other name.");
            alert.setHeaderText("Table name is already in use!");
            alert.show();
        }
    }
    public void deleteButtonClick(ActionEvent event) {
        if(phonebookPicker.getSelectionModel().getSelectedIndex() > -1) {
            JDBCquery.deletePhonebook(phonebookPicker.getValue());
            phonebookPicker.getItems().remove(phonebookPicker.getSelectionModel().getSelectedIndex());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Table deleted successfully!");
            alert.setHeaderText("Table deleted successfully!");
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Table not selected.");
            alert.setContentText("Select table which you want to delete from the list and then press the button again.");
            alert.setHeaderText("No table was selected!");
            alert.show();
        }
    }
    public void editButtonClick(ActionEvent event) throws IOException {
        for (int i = 0; i < selectedRecordInfo.length; i++) {
            selectedRecordInfo[i] = "";
        }
        if (phonebookPicker.getSelectionModel().getSelectedIndex() > -1) {
            currentTable = String.valueOf(phonebookPicker.getValue());
            changeScene("sceneEditTable.fxml", event);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Table not selected.");
            alert.setContentText("Select table which you want to edit from the list and then press the button again.");
            alert.setHeaderText("No table was selected!");
            alert.show();
        }
    }
    public void addRecord(ActionEvent event) throws IOException {
        if((!nameNewField.getText().trim().isEmpty()) && (!lnameNewField.getText().trim().isEmpty()) && (!phoneNewField.getText().trim().isEmpty())) {
            boolean exists = JDBCquery.recordExists(currentTable, nameNewField.getText(), lnameNewField.getText(), phoneNewField.getText());
            if(!exists) {
                JDBCquery.addRecord(currentTable, nameNewField.getText(), lnameNewField.getText(), phoneNewField.getText(),
                        streetNewField.getText(), cityNewField.getText(), postNewField.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("New contact added!");
                alert.setHeaderText("Success!");
                alert.setContentText("New contact was added sucessfully!");
                alert.show();
                nameNewField.setText("");
                lnameNewField.setText("");
                phoneNewField.setText("");
                cityNewField.setText("");
                streetNewField.setText("");
                postNewField.setText("");
            } else {
                ButtonType btnYes = new ButtonType("YES", ButtonBar.ButtonData.YES);
                ButtonType btnNo = new ButtonType("NO", ButtonBar.ButtonData.NO);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to update the contact?", btnYes, btnNo);
                alert.setTitle("Contact already exists.");
                alert.setHeaderText("The contact that you want to add already exists!");
                alert.setContentText("Do you want to update the contact information? Click yes to go to search/edit/delete menu.");

                if(alert.showAndWait().get() == btnYes) {
                    changeScene("sceneSearchEditDel.fxml", event);
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing data.");
            alert.setContentText("You need to fill in at least first name, last name and phone number in order to add new contact.");
            alert.setHeaderText("Insufficient data!");
            alert.show();
        }
    }
    public void searchEditDelButtonClick(ActionEvent event) throws IOException {
        changeScene("sceneSearchEditDel.fxml", event);
    }
    public void searchRecordBtnClick(ActionEvent event) throws IOException {
        System.out.println(textFieldSearch.getText());
        if(!textFieldSearch.getText().trim().isEmpty()) {
            if(rbName.isSelected()) {
                searchCriteria = "firstName";
            } else if (rbLast.isSelected()) {
                searchCriteria = "lastName";
            } else if (rbPhone.isSelected()) {
                searchCriteria = "phoneNumber";
            } else if(rbCity.isSelected()) {
                searchCriteria = "addrCity";
            } else if(rbPost.isSelected()) {
                searchCriteria = "addrPost";
            }
            loadTable(false);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No search criteria set!");
            alert.setHeaderText("No search criteria set!");
            alert.setContentText("Please type in search criteria.");
        }
    }
    public ObservableList<ContactInformation> getContacts(boolean all) {
        ObservableList<ContactInformation> contacts = FXCollections.observableArrayList();
        if(all) {
            JDBCquery.loadGetAll(currentTable, contacts);
        } else {
            JDBCquery.searchRecords(currentTable, searchCriteria, textFieldSearch.getText(), contacts);
        }
        return contacts;
    }
    public void deleteRecord(ActionEvent event) {
        ObservableList<ContactInformation> selectedContact = FXCollections.observableArrayList();
        selectedContact = table.getItems();
        if(!selectedContact.isEmpty()) {
            ContactInformation selected = selectedContact.get(table.getSelectionModel().getSelectedIndex());
            int id = selected.getId();
            JDBCquery.deleteRecord(currentTable, id);
            table.getItems().remove(table.getSelectionModel().getSelectedIndex());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No record selected to delete!");
            alert.setHeaderText("No record selected to delete!");
            alert.setContentText("");
            alert.show();
        }
    }
    public void loadTable(boolean all) {
        idCol.setCellValueFactory(new PropertyValueFactory<ContactInformation, Integer>("id"));
        firstnameCol.setCellValueFactory(new PropertyValueFactory<ContactInformation, String>("name"));
        lastnameCol.setCellValueFactory(new PropertyValueFactory<ContactInformation, String>("sname"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<ContactInformation, String>("number"));
        streetCol.setCellValueFactory(new PropertyValueFactory<ContactInformation, String>("street"));
        cityCol.setCellValueFactory(new PropertyValueFactory<ContactInformation, String>("city"));
        postCol.setCellValueFactory(new PropertyValueFactory<ContactInformation, String>("post"));
        table.setItems(getContacts(all));
    }
    public void backButtonClick(ActionEvent event) throws IOException {
        changeScene("sceneEditTable.fxml", event);
        initialize(myUrl, null);
    }

    public void editRecord(ActionEvent event) throws IOException {
        System.out.println("editrecord");
        ObservableList<ContactInformation> selectedContact = FXCollections.observableArrayList();
        selectedContact = table.getSelectionModel().getSelectedItems();
        if(!selectedContact.isEmpty()) {
            ContactInformation selected = selectedContact.get(table.getSelectionModel().getSelectedIndex());
            selectedRecordInfo[0] = selected.getName();
            selectedRecordInfo[1] = selected.getSname();
            selectedRecordInfo[2] = selected.getNumber();
            selectedRecordInfo[3] = selected.getStreet();
            selectedRecordInfo[4] = selected.getCity();
            selectedRecordInfo[5] = selected.getPost();
            selectedRecordInfo[6] = Integer.toString(selected.getId());
            changeScene("sceneEditRecord.fxml", event);
            //file:/C:/Users/witus/IdeaProjects/PhoneBook/target/classes/com/example/phonebook/sceneStart.fxml
            initialize(myUrl, null);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No selected record!");
            alert.setHeaderText("No selected record!");
            alert.setContentText("Select record to edit");
            alert.show();
        }
    }
    public void saveRecord(ActionEvent event) throws IOException {

        if(!(nameField.getText().trim().isEmpty() && lnameField.getText().trim().isEmpty() && phoneField.getText().trim().isEmpty())) {
            String queryText = "firstName = '" + nameField.getText() +
                    "', lastName = '" + lnameField.getText() + "', phoneNumber = '" + phoneField.getText()
                    + "', addrStreet = '" + streetField.getText() + "', addrCity = '" + cityField.getText() +
                    "', addrPost = '" + postField.getText() + "'";

            JDBCquery.updateRecord(currentTable, Integer.parseInt(selectedRecordInfo[6]), queryText);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Contact updated successfully!");
            alert.setHeaderText("Contact updated successfully!");
            alert.setContentText("");
            nameField.setText("");
            lnameField.setText("");
            phoneField.setText("");
            streetField.setText("");
            cityField.setText("");
            postField.setText("");
            if (alert.showAndWait().get() == ButtonType.OK) {
                changeScene("sceneSearchEditDel.fxml", event);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Insufficient data!");
            alert.setHeaderText("Insufficient data");
            alert.setContentText("Fill in at least First name, Last name and Phone number");
            alert.show();
        }
    }
    public void loadAllRecords(ActionEvent event) {
        loadTable(true);
    }
}