package com.playtika.sales.dao.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@DynamicInsert
@Table(name = "sale_proposition")
public class SalePropositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double price;

    @Column(columnDefinition = "ENUM('OPEN','CLOSED')")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "car_id")
    private CarEntity car;

    @OneToMany(mappedBy = "sale", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<OfferEntity> offers = new HashSet<>();

    public enum Status {
        OPEN, CLOSED;
    }
}