package com.example.dao;

import java.util.List;

public class CompositeProductDao implements ProductDao {
    private final ProductDao memoryDao;
    private final ProductDao jsonDao;
    private final ProductDao mongoDao;

    public CompositeProductDao(ProductDao memoryDao, ProductDao jsonDao, ProductDao mongoDao) {
        this.memoryDao = memoryDao;
        this.jsonDao = jsonDao;
        this.mongoDao = mongoDao;
        syncFromJsonToMemory(); // При старте синхронизируем из JSON в Memory
    }

    @Override
    public void addProduct(Product product) {
        memoryDao.addProduct(product);
        jsonDao.addProduct(product);
        mongoDao.addProduct(product);
    }

    @Override
    public void updateProduct(Product product) {
        memoryDao.updateProduct(product);
        jsonDao.updateProduct(product);
        mongoDao.updateProduct(product);
    }

    @Override
    public void deleteProduct(int id) {
        memoryDao.deleteProduct(id);
        jsonDao.deleteProduct(id);
        mongoDao.deleteProduct(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return memoryDao.getAllProducts(); // Всегда берем из memory
    }

    private void syncFromJsonToMemory() {
        // Очищаем memory и загружаем данные из JSON
        List<Product> jsonProducts = jsonDao.getAllProducts();
        memoryDao.getAllProducts().clear();
        memoryDao.getAllProducts().addAll(jsonProducts);
    }
}