package com.example.dao;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controller {
    private ProductDao productDao;
    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, Integer> quantityColumn;
    @FXML
    private TableColumn<Product, String> tagColumn;
    @FXML
    private TableColumn<Product, String> statusColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField tagField;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ComboBox<String> storageTypeComboBox;

    @FXML
    public void initialize() {
        // Инициализация столбцов таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));
        statusColumn.setCellValueFactory(cellData -> {
            int quantity = cellData.getValue().getQuantity();
            String status = quantity > 50 ? "High" : quantity > 10 ? "Medium" : "Low";
            return new SimpleStringProperty(status);
        });

        // Инициализация ComboBox
        storageTypeComboBox.getItems().addAll("Memory", "JSON", "MongoDB", "All");
        storageTypeComboBox.getSelectionModel().selectFirst();
        storageTypeComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> switchStorageType(newVal));

        // Загрузка данных
        switchStorageType(storageTypeComboBox.getValue());

        // Обработчик выбора в таблице
        productTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        populateFields(newSelection);
                    }
                });
    }

    private void switchStorageType(String type) {
        switch (type) {
            case "Memory":
                productDao = DaoFactory.createProductDao("memory");
                break;
            case "JSON":
                productDao = DaoFactory.createProductDao("json");
                break;
            case "MongoDB":
                productDao = DaoFactory.createProductDao("mongo");
                break;
            case "All":
                productDao = new CompositeProductDao(
                        DaoFactory.createProductDao("memory"),
                        DaoFactory.createProductDao("json"),
                        DaoFactory.createProductDao("mongo")
                );
                break;
        }
        refreshTable();
    }

    private void refreshTable() {
        productList.setAll(productDao.getAllProducts());
        productTable.setItems(productList);
    }

    private void populateFields(Product product) {
        nameField.setText(product.getName());
        quantityField.setText(String.valueOf(product.getQuantity()));
        tagField.setText(product.getTag());
    }

    private void clearFields() {
        nameField.clear();
        quantityField.clear();
        tagField.clear();
    }

    @FXML
    private void handleAdd() {
        try {
            Product product = new Product(
                    0, // ID будет установлен DAO
                    nameField.getText(),
                    Integer.parseInt(quantityField.getText()),
                    tagField.getText()
            );
            productDao.addProduct(product);
            refreshTable();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid quantity (number).");
        }
    }

    @FXML
    private void handleUpdate() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                selectedProduct.setName(nameField.getText());
                selectedProduct.setQuantity(Integer.parseInt(quantityField.getText()));
                selectedProduct.setTag(tagField.getText());
                productDao.updateProduct(selectedProduct);
                refreshTable();
                clearFields();
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid quantity (number).");
            }
        } else {
            showAlert("No Selection", "Please select a product to update.");
        }
    }

    @FXML
    private void handleDelete() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            productDao.deleteProduct(selectedProduct.getId());
            refreshTable();
            clearFields();
        } else {
            showAlert("No Selection", "Please select a product to delete.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}