package com.playtika.sales.dao.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sale_proposition")
@EqualsAndHashCode(exclude = {"car", "offers"})
public class SalePropositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double price;

    @Column(columnDefinition = "ENUM('OPEN','CLOSED')", insertable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private CarEntity car;

    @OneToMany(mappedBy = "sale", orphanRemoval = true)
    private Set<OfferEntity> offers;

    public static enum Status {
        OPEN, CLOSED;
    }
}