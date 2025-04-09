package com.example.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductDaoJsonImpl implements ProductDao {
    private final String filePath;
    private final ObjectMapper mapper;

    public ProductDaoJsonImpl(String filePath) {
        this.filePath = filePath;
        this.mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Создаем файл если его нет
        if (!Files.exists(Paths.get(filePath))) {
            try {
                Files.createDirectories(Paths.get(filePath).getParent());
                mapper.writeValue(new File(filePath), new Product[0]);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create JSON file", e);
            }
        }
    }

    @Override
    public void addProduct(Product product) {
        List<Product> products = new ArrayList<>(getAllProducts());
        product.setId(products.isEmpty() ? 1 : products.get(products.size() - 1).getId() + 1);
        products.add(product);
        saveAllProducts(products);
    }

    @Override
    public void updateProduct(Product product) {
        List<Product> products = getAllProducts();
        products.replaceAll(p -> p.getId() == product.getId() ? product : p);
        saveAllProducts(products);
    }

    @Override
    public void deleteProduct(int id) {
        List<Product> products = getAllProducts();
        products.removeIf(p -> p.getId() == id);
        saveAllProducts(products);
    }

    @Override
    public List<Product> getAllProducts() {
        try {
            Product[] productsArray = mapper.readValue(new File(filePath), Product[].class);
            return new ArrayList<>(Arrays.asList(productsArray));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file", e);
        }
    }

    private void saveAllProducts(List<Product> products) {
        try {
            mapper.writeValue(new File(filePath), products);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save to JSON file", e);
        }
    }

}