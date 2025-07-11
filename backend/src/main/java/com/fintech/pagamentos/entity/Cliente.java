package com.fintech.pagamentos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Cliente {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @Size(min = 11, max = 11, message = "O CPF deve ter exatamemte 11 dígitos.")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotNull(message = "A data nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser uma data no passado.")
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @NotNull(message = "O status de bloqueio é obrigatório.")
    @Size(min = 1, max = 1, message = "O status de bloqueio só pode ter um caracter.")
    @Pattern(regexp = "[AB]", message = "O status de bloqueio de ser 'A' (Ativo) ou 'B' (Bloqueado).")
    @Column(name = "status_bloqueio", nullable = false, length = 1)
    private Character statusBloqueio; // 'A' para Ativo, 'B' para Bloqueado

    @NotNull(message = "O limite de crédito é obrigatório.")
    @DecimalMin(value = "0.00", inclusive = true, message = "O limite de crédito não pode ser negativo.")
    @Column(name = "limite_credito", nullable = false, precision = 10, scale = 2)
    private BigDecimal limiteCredito;

}
