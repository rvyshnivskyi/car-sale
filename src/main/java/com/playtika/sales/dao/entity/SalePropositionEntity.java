package com.playtika.sales.dao.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "sale_proposition")
public class SalePropositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(notes = "The database generated sale proposition ID")
    private Long id;

    @ApiModelProperty(notes = "The price amount which car owner want to get for his car")
    private double price;

    @Enumerated(EnumType.STRING)
    @ApiModelProperty(notes = "The status of specific sale proposition")
    private Status status = Status.OPEN;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "car_id")
    @ApiModelProperty(notes = "ID of car for which specific sale proposition was created")
    private CarEntity car;

    @OneToMany(mappedBy = "sale", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @ApiModelProperty(notes = "Set of offers which were proposed for specific sale proposition")
    private Set<OfferEntity> offers = new HashSet<>();

    public enum Status {
        OPEN, CLOSED;
    }
}