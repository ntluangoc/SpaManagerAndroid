package com.example.demoappspa.Entity;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String imgProduct;
    private String nameProduct;
    private String price;
    private String skin;
    private String description;
    private String author;
    public Product() {
    }

    public Product(String id, String imgProduct, String nameProduct, String price, String skin, String description) {
        this.id = id;
        this.imgProduct = imgProduct;
        this.nameProduct = nameProduct;
        this.price = price;
        this.skin = skin;
        this.description = description;
    }

    public Product(String id, String imgProduct, String nameProduct, String price, String skin, String description, String author) {
        this.id = id;
        this.imgProduct = imgProduct;
        this.nameProduct = nameProduct;
        this.price = price;
        this.skin = skin;
        this.description = description;
        this.author = author;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setImgProduct(String imgProduct) {
        this.imgProduct = imgProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getSkin() {
        return skin;
    }

    public String getImgProduct() {
        return imgProduct;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", imgProduct='" + imgProduct + '\'' +
                ", nameProduct='" + nameProduct + '\'' +
                ", price='" + price + '\'' +
                ", skin='" + skin + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
