package com.playtika.sales.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
@Table(name = "offer")
@EqualsAndHashCode(exclude = {"sale", "buyer"})
public class OfferEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date date;
    private Double price;

    @Column(columnDefinition = "ENUM('ACTIVE','ACCEPTED','DECLINED')")
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "sale_proposition_id")
    private SalePropositionEntity sale;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private PersonEntity buyer;

    public static enum Status {
        ACTIVE, ACCEPTED, DECLINED;
    }
}
