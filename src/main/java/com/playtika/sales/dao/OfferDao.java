package com.playtika.sales.dao;

import com.playtika.sales.dao.entity.OfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfferDao extends JpaRepository<OfferEntity, Long> {
    @Query(value = "SELECT offer from OfferEntity offer " +
            "where offer.sale.car.id = :id and offer.status = :status")
    List<OfferEntity> findAllOffersByCarIdAndStatus(@Param("id") long id, @Param("status") OfferEntity.Status status);

    Optional<OfferEntity> findFirstByIdAndStatus(long offerId, OfferEntity.Status active);
}
