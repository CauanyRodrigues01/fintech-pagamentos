package com.fintech.pagamentos.repository;

import com.fintech.pagamentos.entity.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FaturaRepository extends JpaRepository<Fatura, UUID> {

    @Query("SELECT f FROM Fatura f JOIN FETCH f.cliente")
    List<Fatura> findAllWithCliente();

    @Query("SELECT f FROM Fatura f JOIN FETCH f.cliente WHERE f.id = :faturaId")
    Optional<Fatura> findByIdWithCliente(@Param("faturaId") UUID faturaId);

    @Query("SELECT f FROM Fatura f JOIN FETCH f.cliente WHERE f.cliente.id = :clienteId")
    List<Fatura> findByClienteIdWithCliente(@Param("clienteId") UUID clienteId);

    @Query("SELECT f FROM Fatura f JOIN FETCH f.cliente WHERE f.status = :status")
    List<Fatura> findByStatusWithCliente(@Param("status") Character status);

    @Query("SELECT f FROM Fatura f JOIN FETCH f.cliente WHERE f.status = :status AND f.dataVencimento < :dataVencimento")
    List<Fatura> findByStatusAndDataVencimentoBefore(@Param("status") Character status, @Param("dataVencimento") LocalDate dataVencimento);

}
