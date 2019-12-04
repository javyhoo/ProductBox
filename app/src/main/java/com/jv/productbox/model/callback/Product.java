package com.jv.productbox.model.callback;

import java.util.List;

public class Product {

    public String getProductid() {
        return productid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedate() {
        return createdate;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getUserid() {
        return userid;
    }

    public List<String> getImags() {
        return imags;
    }

    private String productid;
    private String name;
    private String description;
    private String createdate;
    private String barcode;
    private String userid;
    private List<String> imags;
}
