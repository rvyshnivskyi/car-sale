package com.playtika.sales.dao.entity;

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
    private Long id;

    private String firstName;
    private String lastName;
    private String city;
    private String phoneNumber;

    @OneToMany(mappedBy = "owner")
    private Set<CarEntity> cars = new HashSet<>();

    @OneToMany(mappedBy = "buyer")
    private Set<OfferEntity> offers = new HashSet<>();
}
