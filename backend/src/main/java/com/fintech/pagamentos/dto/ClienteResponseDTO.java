package com.fintech.pagamentos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class ClienteResponseDTO {

    private UUID id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private Character statusBloqueio;
    private BigDecimal limiteCredito;

}
