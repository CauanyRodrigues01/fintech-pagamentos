package com.fintech.pagamentos.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Para POST /clientes e PUT /clientes/{id}
// Este DTO terá os campos que podem ser recebidos na requisição para criar ou atualizar um cliente.
@Data @NoArgsConstructor @AllArgsConstructor
public class ClienteRequestDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @Size(min = 11, max = 11, message = "O CPF deve ter exatamemte 11 dígitos.")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter apenas dígitos.")
    private String cpf;

    @NotNull(message = "A data nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser uma data no passado.")
    private LocalDate dataNascimento;

    @NotNull(message = "O status de bloqueio é obrigatório.")
    @Size(min = 1, max = 1, message = "O status de bloqueio só pode ter um caracter.")
    @Pattern(regexp = "[AB]", message = "O status de bloqueio de ser 'A' (Ativo) ou 'B' (Bloqueado).")
    private Character statusBloqueio; // 'A' para Ativo, 'B' para Bloqueado

    @NotNull(message = "O limite de crédito é obrigatório.")
    @DecimalMin(value = "0.00", inclusive = true, message = "O limite de crédito não pode ser negativo.")
    private BigDecimal limiteCredito;
}
