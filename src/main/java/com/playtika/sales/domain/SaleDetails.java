package com.playtika.sales.domain;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Value
@Builder
public class SaleDetails {
    @Min(0)
    private double price;
    @NotBlank
    private String ownerFirstName;
    @NotBlank
    private String ownerPhoneNumber;

    private String ownerLastName;
    private Long carId;
}
