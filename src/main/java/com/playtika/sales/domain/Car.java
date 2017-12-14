package com.playtika.sales.domain;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Value
@Builder
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
