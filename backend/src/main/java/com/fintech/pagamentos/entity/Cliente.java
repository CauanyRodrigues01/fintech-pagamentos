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
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @Size(min = 11, max = 11, message = "O CPF deve ter exatamemte 11 dígitos.")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter apenas dígitos.")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotNull(message = "A data nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser uma data no passado.")
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @NotNull(message = "O status de bloqueio é obrigatório.")
    @ValidCharStatus(allowedValues = {'A', 'B'}, message = "O status de bloqueio deve ser 'A' (Ativo) ou 'B' (Bloqueado).")
    @Column(name = "status_bloqueio", nullable = false, length = 1)
    private Character statusBloqueio; // 'A' para Ativo, 'B' para Bloqueado

    @NotNull(message = "O limite de crédito é obrigatório.")
    @DecimalMin(value = "0.00", inclusive = true, message = "O limite de crédito não pode ser negativo.")
    @Column(name = "limite_credito", nullable = false, precision = 10, scale = 2)
    private BigDecimal limiteCredito;

}
