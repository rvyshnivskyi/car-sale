package com.playtika.sales.dao;

import com.playtika.sales.dao.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarDao extends JpaRepository<CarEntity, Long> {
}
