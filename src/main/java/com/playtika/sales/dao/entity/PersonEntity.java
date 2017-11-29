package com.playtika.sales.dao.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private Set<CarEntity> cars;

    @OneToMany(mappedBy = "buyer")
    private Set<OfferEntity> offers;
}
