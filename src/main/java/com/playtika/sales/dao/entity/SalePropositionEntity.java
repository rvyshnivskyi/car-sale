package com.playtika.sales.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "sale_proposition")
public class SalePropositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double price;

    @Column(columnDefinition = "ENUM('OPEN','CLOSED')", insertable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "car_id")
    private CarEntity car;

    @OneToMany(mappedBy = "sale", orphanRemoval = true)
    private Set<OfferEntity> offers;

    public static enum Status {
        OPEN, CLOSED;
    }
}