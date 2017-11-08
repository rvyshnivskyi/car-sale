package com.playtika.sales.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.NotBlank;

@Data
@AllArgsConstructor
public class SaleDetails {
    @NonNull
    private Long carId;
    @NonNull
    private Double price;
    @NotBlank
    private String ownerFirstName;
    @NotBlank
    private String ownerPhoneNumber;

    private String ownerLastName;
}
