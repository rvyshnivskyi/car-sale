package com.playtika.sales.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

@Data
@EqualsAndHashCode(exclude = "id")
public class Car {
    private Long id;
    @NotBlank
    private String brand;
    @NotBlank
    private String color;
    @Min(0)
    private int age;
    @NotBlank
    private String number;
}
