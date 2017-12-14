package com.playtika.sales.dao;

import com.playtika.sales.dao.entity.SalePropositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalePropositionDao extends JpaRepository<SalePropositionEntity, Long> {
    List<SalePropositionEntity> findByCarIdAndStatus(Long carId, SalePropositionEntity.Status status);

    int deleteByCarIdAndStatus(Long carId, SalePropositionEntity.Status status);
}
