package com.playtika.sales.dao;

import com.playtika.sales.dao.entity.SalePropositionEntity;
import com.playtika.sales.dao.entity.SalePropositionEntity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalePropositionDao extends JpaRepository<SalePropositionEntity, Long> {
    List<SalePropositionEntity> findByCarIdAndStatus(Long carId, Status status);

    int deleteByCarIdAndStatus(Long carId, Status status);
}
