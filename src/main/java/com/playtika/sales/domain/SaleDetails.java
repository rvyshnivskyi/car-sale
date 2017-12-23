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
public class SaleDetails {
    @Min(0)
    @ApiModelProperty(notes = "The price amount which car owner want to get for his car")
    private double price;
    @NotBlank
    @ApiModelProperty(notes = "The first name of car owner")
    private String ownerFirstName;

    @NotBlank
    @ApiModelProperty(notes = "The contact number of car owner")
    private String ownerPhoneNumber;

    @ApiModelProperty(notes = "The first name of car owner")
    private String ownerLastName;

    @ApiModelProperty(notes = "ID of car for which specific sale proposition was created")
    private Long carId;
}
