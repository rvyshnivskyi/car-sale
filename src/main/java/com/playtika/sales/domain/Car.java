package com.playtika.sales.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Getter
@Builder
public class Car {
    @ApiModelProperty(notes = "The database generated car ID")
    private Long id;
    @NotBlank
    @ApiModelProperty(notes = "Brand name of the car")
    private String brand;
    @NotBlank
    @ApiModelProperty(notes = "The color of car")
    private String color;
    @Min(0)
    @ApiModelProperty(notes = "The year when car was produced")
    private int year;
    @NotBlank
    @ApiModelProperty(notes = "Plate number of the car")
    private String number;
}