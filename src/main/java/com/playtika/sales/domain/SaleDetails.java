package com.playtika.sales.domain;

import lombok.Data;

@Data
public class SaleDetails {
    private double price;
    private String ownerFirstName;
    private String ownerLastName;
    private Long ownerPhoneNumber;

    public SaleDetails(double price, String ownerFirstName, String ownerLastName, Long ownerPhoneNumber) {
        this.price = price;
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.ownerPhoneNumber = ownerPhoneNumber;
    }
}
