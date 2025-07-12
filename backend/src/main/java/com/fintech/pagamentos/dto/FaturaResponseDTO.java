package com.fintech.pagamentos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class FaturaResponseDTO {

    private UUID id;
    private UUID clienteId;
    private String clienteNome;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private BigDecimal valor;
    private Character status;

}
