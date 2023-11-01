package com.example.middletask.Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

// Класс, используемый для связи интерфейса приложения и кода
// Обозначает сущность заказ с полями id, название, заказчик, дата заказа и цена
public class Order {
    private SimpleIntegerProperty id;
    private SimpleStringProperty product;
    private SimpleStringProperty client;
    private SimpleStringProperty date;
    private SimpleStringProperty price;

    public Order(int id, String product, String client, String date, String price){
        this.id = new SimpleIntegerProperty(id);
        this.product = new SimpleStringProperty(product);
        this.client = new SimpleStringProperty(client);
        this.date = new SimpleStringProperty(date);
        this.price = new SimpleStringProperty(price);
    }

    public Order(String product, String client, String date, String price){
        this.product = new SimpleStringProperty(product);
        this.client = new SimpleStringProperty(client);
        this.date = new SimpleStringProperty(date);
        this.price = new SimpleStringProperty(price);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getProduct() {
        return product.get();
    }

    public SimpleStringProperty productProperty() {
        return product;
    }

    public void setProduct(String product) {
        this.product.set(product);
    }

    public String getClient() {
        return client.get();
    }

    public SimpleStringProperty clientProperty() {
        return client;
    }

    public void setClient(String client) {
        this.client.set(client);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getPrice() {
        return price.get();
    }

    public SimpleStringProperty priceProperty() {
        return price;
    }

    public void setPrice(String price) {
        this.price.set(price);
    }
}

