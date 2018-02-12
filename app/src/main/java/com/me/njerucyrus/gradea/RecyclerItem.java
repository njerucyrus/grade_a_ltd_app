package com.me.njerucyrus.gradea;

/**
 * Created by njerucyrus on 1/25/18.
 */

public class RecyclerItem {
    private int id;
    private String receiptNo;
    private String phoneNumber;
    private String authorisedBy;
    private String vatNo;
    private String kraPin;
    private String payeeName;
    private String products;
    private String description;
    private String price;
    private String date;
    private int isArchived;
    private String mPesa;
    private String invoiceNo;

    public RecyclerItem(){}

    public RecyclerItem(String receiptNo, String vatNo, String kraPin,
                        String payeeName, String products, String description,
                        String price, String date, String invoiceNo) {
        this.receiptNo = receiptNo;
        this.vatNo = vatNo;
        this.kraPin = kraPin;
        this.payeeName = payeeName;
        this.products = products;
        this.description = description;
        this.price = price;
        this.date = date;
        this.invoiceNo = invoiceNo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAuthorisedBy() {
        return authorisedBy;
    }

    public void setAuthorisedBy(String authorisedBy) {
        this.authorisedBy = authorisedBy;
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

    public int getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(int isArchived) {
        this.isArchived = isArchived;
    }

    public String getmPesa() {
        return mPesa;
    }

    public void setmPesa(String mPesa) {
        this.mPesa = mPesa;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }
}
