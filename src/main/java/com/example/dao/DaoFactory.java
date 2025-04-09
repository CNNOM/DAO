package com.example.dao;

public class DaoFactory {
    public static ProductDao createProductDao(String type) {
        switch (type.toLowerCase()) {
            case "memory":
                return new ProductDaoImpl();
            case "mongo":
                return new ProductDaoMongoImpl(
                        "mongodb://localhost:27017",
                        "productDB",
                        "products"
                );
            case "json":
                return new ProductDaoJsonImpl("data/products.json");
            default:
                throw new IllegalArgumentException("Unknown DAO type: " + type);
        }
    }
}