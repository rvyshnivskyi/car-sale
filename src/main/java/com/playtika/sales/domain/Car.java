package com.playtika.sales.domain;

import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class Car {
    private Long id;
    @NotBlank
    private String brand;
    @NotBlank
    private String color;
    @NonNull
    private Integer age;
    @NotBlank
    private String number;
}
