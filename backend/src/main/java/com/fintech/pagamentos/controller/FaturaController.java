package com.fintech.pagamentos.controller;

import com.fintech.pagamentos.dto.ClienteResponseDTO;
import com.fintech.pagamentos.dto.FaturaPaymentRequestDTO;
import com.fintech.pagamentos.dto.FaturaResponseDTO;
import com.fintech.pagamentos.entity.Fatura;
import com.fintech.pagamentos.service.FaturaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/faturas")
public class FaturaController {

    private final FaturaService faturaService;

    public FaturaController(FaturaService faturaService) {
        this.faturaService = faturaService;
    }

    @GetMapping
    public ResponseEntity<List<FaturaResponseDTO>> listarTodasFaturas() {
        List<FaturaResponseDTO> faturas = faturaService.listarTodasFaturas();
        return ResponseEntity.ok(faturas);
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<List<FaturaResponseDTO>> listarFaturasDoCliente(@PathVariable("clienteId") UUID clienteId) {
        List<FaturaResponseDTO> faturas = faturaService.listarFaturasPorClienteId(clienteId);
        return ResponseEntity.ok(faturas);
    }

    @PutMapping("/{faturaId}/pagamento")
    public ResponseEntity<FaturaResponseDTO> registrarPagamentoFatura(@PathVariable("faturaId") UUID faturaId,
                                                           @Valid @RequestBody FaturaPaymentRequestDTO paymentDto) {
        FaturaResponseDTO faturaPaga = faturaService.registrarPagamento(faturaId, paymentDto);
        return ResponseEntity.ok(faturaPaga);
    }

    @GetMapping("/atrasadas")
    public ResponseEntity<List<FaturaResponseDTO>> listarFaturasAtrasadas() {
        List<FaturaResponseDTO> faturasAtradas = faturaService.listarFaturasAtrasadas();
        return ResponseEntity.ok(faturasAtradas);
    }

}
