package com.playtika.sales.dao.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "car")
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated car ID")
    private Long id;

    @ApiModelProperty(notes = "Plate number of the car")
    private String plateNumber;
    @ApiModelProperty(notes = "Brand name of the car")
    private String brand;
    @ApiModelProperty(notes = "Year when car was produced")
    private int year;
    @ApiModelProperty(notes = "The color of car")
    private String color;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_id")
    @ApiModelProperty(notes = "The car owner ID")
    private PersonEntity owner;

    @OneToMany(mappedBy = "car")
    @ApiModelProperty(notes = "Set of the sale details of specific car")
    private Set<SalePropositionEntity> sales;
}

