package com.playtika.sales.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

@Data
@EqualsAndHashCode(exclude = "cardId")
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
