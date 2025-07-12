package com.fintech.pagamentos.service;

import com.fintech.pagamentos.dto.FaturaPaymentRequestDTO;
import com.fintech.pagamentos.dto.FaturaResponseDTO;
import com.fintech.pagamentos.entity.Fatura;
import com.fintech.pagamentos.repository.FaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FaturaService {

    private final FaturaRepository faturaRepository;

    @Autowired
    public FaturaService(FaturaRepository faturaRepository) {
        this.faturaRepository = faturaRepository;
    }

    @Transactional
    public List<FaturaResponseDTO> listarFaturasPorClienteId(UUID clienteId) {

        List<Fatura> faturas = faturaRepository.findByClienteIdWithCliente(clienteId);

        return faturas.stream().map(fatura -> {
            FaturaResponseDTO dto = new FaturaResponseDTO();
            dto.setId(fatura.getId());
            dto.setClienteId(fatura.getCliente().getId()); // Acessa o ID do cliente
            dto.setClienteNome(fatura.getCliente().getNome()); // Acessa o nome do cliente
            dto.setDataVencimento(fatura.getDataVencimento());
            dto.setDataPagamento(fatura.getDataPagamento());
            dto.setValor(fatura.getValor());
            dto.setStatus(fatura.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }


    @Transactional
    public FaturaResponseDTO registrarPagamento(UUID faturaId, FaturaPaymentRequestDTO paymentDTO) {

        Fatura fatura = faturaRepository.findByIdWithCliente(faturaId)
                .orElseThrow(() -> new RuntimeException("Fatura não encontrada com ID: " + faturaId));

        if (fatura.getStatus() == 'P') {
            throw new RuntimeException("Fatura já está paga.");
        }

        fatura.setDataPagamento(paymentDTO.getDataPagamento());
        fatura.setStatus('P');

        Fatura faturaAtualizada = faturaRepository.save(fatura);

        FaturaResponseDTO dto = new FaturaResponseDTO();
        dto.setId(faturaAtualizada.getId());
        dto.setClienteId(faturaAtualizada.getCliente().getId());
        dto.setClienteNome(faturaAtualizada.getCliente().getNome());
        dto.setDataVencimento(faturaAtualizada.getDataVencimento());
        dto.setDataPagamento(faturaAtualizada.getDataPagamento());
        dto.setValor(faturaAtualizada.getValor());
        dto.setStatus(faturaAtualizada.getStatus());

        return dto;
    }

    @Transactional
    public List<FaturaResponseDTO> listarFaturasAtrasadas() {
        List<Fatura> faturas = faturaRepository.findByStatusWithCliente('A');

        return faturas.stream().map(fatura -> {
            FaturaResponseDTO dto = new FaturaResponseDTO();
            dto.setId(fatura.getId());
            dto.setClienteId(fatura.getCliente().getId()); // Acessa o ID do cliente
            dto.setClienteNome(fatura.getCliente().getNome()); // Acessa o nome do cliente
            dto.setDataVencimento(fatura.getDataVencimento());
            dto.setDataPagamento(fatura.getDataPagamento());
            dto.setValor(fatura.getValor());
            dto.setStatus(fatura.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

}
