package com.playtika.sales.dao.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "person")
public class PersonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated person ID")
    private Long id;

    @ApiModelProperty(notes = "The first name of person")
    private String firstName;
    @ApiModelProperty(notes = "The last name of person")
    private String lastName;
    @ApiModelProperty(notes = "The city from where person is")
    private String city;
    @ApiModelProperty(notes = "The contact phone number of person")
    private String phoneNumber;

    @OneToMany(mappedBy = "owner")
    @ApiModelProperty(notes = "Set of own cars of specific person")
    private Set<CarEntity> cars = new HashSet<>();

    @OneToMany(mappedBy = "buyer")
    @ApiModelProperty(notes = "Set of car buying offers which were proposed by specific person")
    private Set<OfferEntity> offers = new HashSet<>();
}
