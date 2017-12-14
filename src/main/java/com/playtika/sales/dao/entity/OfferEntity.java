package com.playtika.sales.dao.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Data
@NoArgsConstructor
@DynamicInsert
@Table(name = "offer")
public class OfferEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date date;
    private double price;

    @Enumerated(value = EnumType.STRING)
    private Status status = Status.ACTIVE;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sale_proposition_id")
    private SalePropositionEntity sale;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "buyer_id")
    private PersonEntity buyer;

    public enum Status {
        ACTIVE,
        ACCEPTED,
        DECLINED
    }
}
