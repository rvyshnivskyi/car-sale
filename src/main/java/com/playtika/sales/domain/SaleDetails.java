package com.playtika.sales.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
public class SaleDetails {
    private Long carId;
    @Min(0)
    private double price;
    @NotBlank
    private String ownerFirstName;
    @NotBlank
    private String ownerPhoneNumber;

    private String ownerLastName;
}
