package com.fintech.pagamentos.repository;

import com.fintech.pagamentos.entity.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FaturaRepository extends JpaRepository<Fatura, UUID> {
}
