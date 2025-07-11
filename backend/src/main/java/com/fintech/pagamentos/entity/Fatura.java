package com.fintech.pagamentos.entity;

import com.fintech.pagamentos.validation.ValidCharStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
public class Fatura {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @NotNull(message = "A fatura deve estar associada a um cliente.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull(message = "A data de vencimento é obrigatória.")
    @FutureOrPresent(message = "A data de vencimento deve ser igual ou posterior à data atual.")
    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @PastOrPresent(message = "A data de pagamento não pode ser uma data futura.")
    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @NotNull(message = "O valor da fatura é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor da fatura deve ser maior que zero.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "O status da fatura é obrigatório")
    @ValidCharStatus(allowedValues = {'P', 'A', 'B'}, message = "O status da fatura deve ser 'P' (Paga), 'A' (Atrasada) ou 'B' (Aberta).")
    @Column(nullable = false, length = 1)
    private Character status;

}
