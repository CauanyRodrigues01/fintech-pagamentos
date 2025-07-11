package com.fintech.pagamentos.repository;


import com.fintech.pagamentos.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    List<Cliente> findByStatusBloqueio(Character statusBloqueio);

}
