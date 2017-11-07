package com.playtika.sales.domain;

import lombok.Data;

@Data
public class Car {
    private static int CURRENT_CAR_ID = 0;
    private int id;
    private String brand;
    private String color;
    private int age;
    private SaleDetails saleDetails;

    public Car() {
        this.id = ++CURRENT_CAR_ID;
    }

    public int getId() {
        return id;
    }

    public SaleDetails getSaleDetails() {
        return saleDetails;
    }

    public void setSaleDetails(SaleDetails saleDetails) {
        this.saleDetails = saleDetails;
    }
}
