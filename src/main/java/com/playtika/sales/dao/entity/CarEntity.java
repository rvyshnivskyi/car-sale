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
@Table(name = "car")
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String plateNumber;
    private String brand;
    private int year;
    private String color;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_id")
    private PersonEntity owner;

    @OneToMany(mappedBy = "car")
    private Set<SalePropositionEntity> sales;
}

