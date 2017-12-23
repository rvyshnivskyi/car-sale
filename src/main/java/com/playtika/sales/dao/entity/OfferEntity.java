package com.playtika.sales.dao.entity;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(notes = "The database generated offer ID")
    private Long id;

    @ApiModelProperty(notes = "The date when offer was proposed")
    private Date date;
    @ApiModelProperty(notes = "The price which customer ready to pay for the specific car")
    private double price;

    @Enumerated(value = EnumType.STRING)
    @ApiModelProperty(notes = "The status of specific offer")
    private Status status = Status.ACTIVE;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sale_proposition_id")
    @ApiModelProperty(notes = "The sale proposition to which specific offer was proposed")
    private SalePropositionEntity sale;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "buyer_id")
    @ApiModelProperty(notes = "ID of the person who proposed specific offer")
    private PersonEntity buyer;

    public enum Status {
        ACTIVE,
        ACCEPTED,
        DECLINED
    }
}
