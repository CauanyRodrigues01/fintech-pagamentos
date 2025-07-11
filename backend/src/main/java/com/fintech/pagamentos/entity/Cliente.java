package com.fintech.pagamentos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class Cliente {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "status_bloqueio", nullable = false, length = 1)
    private Character statusBloqueio; // 'A' para Ativo, 'B' para Bloqueado

    @Column(name = "limite_credito", nullable = false, precision = 10, scale = 2)
    private BigDecimal limiteCredito;

}
