package com.playtika.sales.dao.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "person")
@EqualsAndHashCode(exclude = {"cars", "offers"})
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
