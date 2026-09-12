package com.example.currencywatcher.repository;

import com.example.currencywatcher.domain.AlertsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertsRepository extends JpaRepository<AlertsEntity, Long> {

    List<AlertsEntity> findByActiveTrueOrderByIdAsc();
}
