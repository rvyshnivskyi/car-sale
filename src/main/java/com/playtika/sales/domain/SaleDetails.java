package com.playtika.sales.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Getter
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
