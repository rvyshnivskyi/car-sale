package com.playtika.sales.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Offer {
    @ApiModelProperty(notes = "The database generated offer ID")
    private Long id;

    @Min(0)
    @ApiModelProperty(notes = "The price amount which car buyer can pay for this car")
    private double price;
    @NotBlank
    @ApiModelProperty(notes = "The first name of car buyer")
    private String buyerFirstName;

    @NotBlank
    @ApiModelProperty(notes = "The contact number of car buyer")
    private String buyerPhoneNumber;

    @ApiModelProperty(notes = "The first name of car buyer")
    private String buyerLastName;
}
