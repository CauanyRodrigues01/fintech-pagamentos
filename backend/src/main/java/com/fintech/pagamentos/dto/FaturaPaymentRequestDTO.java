package com.fintech.pagamentos.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Para PUT /faturas/{id}/pagamento
// Este DTO tem o campo que deve ser recebido para confirmar o pagamento.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaturaPaymentRequestDTO {

    @NotNull(message = "A data de pagamento é obrigatória.")
    @PastOrPresent(message = "A data de pagamento não pode ser uma data futura.")
    private LocalDate dataPagamento;

}
