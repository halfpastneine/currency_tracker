package com.example.currencywatcher.repository;

import com.example.currencywatcher.domain.CurrentPriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentPriceRepository extends JpaRepository<CurrentPriceEntity, Long> {
}
