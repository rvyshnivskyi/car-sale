package com.playtika.sales.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Getter
@Setter
@Table(name = "offer")
public class OfferEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date date;
    private double price;

    @Column(columnDefinition = "ENUM")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sale_proposition_id")
    private SalePropositionEntity sale;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "buyer_id")
    private PersonEntity buyer;

    public static enum Status {
        ACTIVE, ACCEPTED, DECLINED;
    }
}
