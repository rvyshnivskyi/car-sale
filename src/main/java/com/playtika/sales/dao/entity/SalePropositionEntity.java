package com.playtika.sales.dao.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@DynamicInsert
@Table(name = "sale_proposition")
public class SalePropositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double price;

    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "car_id")
    private CarEntity car;

    @OneToMany(mappedBy = "sale", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<OfferEntity> offers = new HashSet<>();

    public enum Status {
        OPEN, CLOSED;
    }
}