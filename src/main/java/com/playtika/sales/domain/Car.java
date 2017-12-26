package com.playtika.sales.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Getter
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
