package com.example.finaltraining1;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private double finalPrice;
    private String dateStr;

public Product (int id,String name,double price,int quantity,double finalPrice,String date) {
this.id = id;
this.name = name;
this.price = price;
this.quantity = quantity;
this.finalPrice = finalPrice;
this.dateStr = date;
}
public int getId() {
return id;
}
public String getName() {
    return name;
}
public double getPrice() {
    return price;
}
public int getQuantity() {
    return quantity;
}
public double getFinalPrice() {
    return finalPrice;
}
public String getDate() {
    return dateStr;
}
public void setName(String name){
    this.name = name;
}
}
