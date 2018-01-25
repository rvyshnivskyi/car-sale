package com.playtika.sales.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Person {
    @ApiModelProperty(notes = "The database generated person ID")
    private Long id;

    @NotBlank
    @ApiModelProperty(notes = "The first name of person")
    private String firstName;

    @NotBlank
    @ApiModelProperty(notes = "The contact number of person")
    private String phoneNumber;

    @ApiModelProperty(notes = "The first name of person")
    private String lastName;
}
