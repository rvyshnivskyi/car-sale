package com.playtika.sales.dao.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "car")
@EqualsAndHashCode(exclude = {"owner", "sales"})
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;

    private String plateNumber;
    private String brand;
    private Integer year;
    private String color;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private PersonEntity owner;

    @OneToMany(mappedBy = "car")
    private Set<SalePropositionEntity> sales;
}

