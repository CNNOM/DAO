package com.example.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ProductDaoMongoImpl implements ProductDao {
    private final MongoCollection<Document> collection;

    public ProductDaoMongoImpl(String connectionString, String dbName, String collectionName) {
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(dbName);
        this.collection = database.getCollection(collectionName);
    }

    @Override
    public void addProduct(Product product) {
        Document doc = new Document()
                .append("_id", product.getId())  // Явно указываем числовой ID
                .append("name", product.getName())
                .append("quantity", product.getQuantity())
                .append("tag", product.getTag());
        collection.insertOne(doc);
    }

    @Override
    public void updateProduct(Product product) {
        collection.updateOne(
                new Document("_id", product.getId()),  // Используем числовой ID
                new Document("$set", new Document()
                        .append("name", product.getName())
                        .append("quantity", product.getQuantity())
                        .append("tag", product.getTag()))
        );
    }

    @Override
    public void deleteProduct(int id) {
        collection.deleteOne(new Document("_id", id));  // Используем числовой ID
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        for (Document doc : collection.find()) {
            products.add(new Product(
                    doc.getInteger("_id"),  // Получаем числовой ID
                    doc.getString("name"),
                    doc.getInteger("quantity"),
                    doc.getString("tag")
            ));
        }
        return products;
    }
}