package com.me.njerucyrus.gradea;

/**
 * Created by njerucyrus on 1/25/18.
 */

public class RecyclerItem {
    private String receiptNo;
    private String vatNo;
    private String kraPin;
    private String payeeName;
    private String products;
    private String description;
    private String price;
    private String date;

    public RecyclerItem(){}

    public RecyclerItem(String receiptNo, String vatNo, String kraPin,
                        String payeeName, String products, String description,
                        String price, String date) {
        this.receiptNo = receiptNo;
        this.vatNo = vatNo;
        this.kraPin = kraPin;
        this.payeeName = payeeName;
        this.products = products;
        this.description = description;
        this.price = price;
        this.date = date;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getVatNo() {
        return vatNo;
    }

    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }

    public String getKraPin() {
        return kraPin;
    }

    public void setKraPin(String kraPin) {
        this.kraPin = kraPin;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
