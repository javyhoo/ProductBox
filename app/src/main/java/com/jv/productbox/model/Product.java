package com.jv.productbox.model;

import java.util.List;

public class Product {

    private String productid;
    private String productname;
    private String barcode;
    private String date;
    private String user;
    private String description;
    private List<String> imags;

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImags() {
        return imags;
    }

    public void setImags(List<String> imags) {
        this.imags = imags;
    }

}
