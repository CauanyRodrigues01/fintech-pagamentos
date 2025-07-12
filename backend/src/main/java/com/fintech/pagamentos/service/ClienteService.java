package com.fintech.pagamentos.service;

import com.fintech.pagamentos.dto.ClienteRequestDTO;
import com.fintech.pagamentos.dto.ClienteResponseDTO;
import com.fintech.pagamentos.entity.Cliente;
import com.fintech.pagamentos.entity.Fatura;
import com.fintech.pagamentos.repository.ClienteRepository;
import com.fintech.pagamentos.repository.FaturaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final FaturaRepository faturaRepository;
    private final ModelMapper modelMapper;

    public ClienteService(ClienteRepository clienteRepository, FaturaRepository faturaRepository, ModelMapper modelMapper) {
        this.clienteRepository = clienteRepository;
        this.faturaRepository = faturaRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public List<ClienteResponseDTO> listarTodosClientes() {
        return clienteRepository.findAll().stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public ClienteResponseDTO cadastrarCliente(ClienteRequestDTO clienteDto) {

        Cliente cliente = modelMapper.map(clienteDto, Cliente.class);

        // Por default os clientes tem statusBloqueio Ativo (A)
        if (cliente.getStatusBloqueio() == null) {
            cliente.setStatusBloqueio('A');
        }
        if (cliente.getLimiteCredito() == null) {
            cliente.setLimiteCredito(BigDecimal.ZERO);
        }
        // Regra de Negócio: Se o cliente for bloqueado, limite de crédito vira 0
        if (cliente.getStatusBloqueio() == 'B' && cliente.getLimiteCredito().compareTo(BigDecimal.ZERO) != 0) {
            cliente.setLimiteCredito(BigDecimal.ZERO);
        }

        Cliente salvo = clienteRepository.save(cliente);

        return modelMapper.map(salvo, ClienteResponseDTO.class); // Mapeia entidade salva para DTO de resposta
    }

    @Transactional
    public Optional<ClienteResponseDTO> buscarClientePorId(UUID id) {
        return clienteRepository.findById(id)
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class));
    }

    @Transactional
    public ClienteResponseDTO atualizarCliente(UUID id, ClienteRequestDTO clienteDto) {

        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));

        modelMapper.map(clienteDto, clienteExistente); // Mapeia DTO de requisição para entidade existente
        // Regra de Negócio: Se o cliente for bloqueado, limite de crédito vira 0
        if (clienteExistente.getStatusBloqueio() == 'B' && clienteExistente.getLimiteCredito().compareTo(BigDecimal.ZERO) != 0) {
            clienteExistente.setLimiteCredito(BigDecimal.ZERO);
        }

        Cliente atualizado = clienteRepository.save(clienteExistente);

        return modelMapper.map(atualizado, ClienteResponseDTO.class);

    }

    @Transactional
    public List<ClienteResponseDTO> listarClientesBloqueados() {
        return clienteRepository.findByStatusBloqueio('B').stream()
                .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void verificarEBloquearClientesAtrasados() {
        System.out.println("Executando job de verificação e bloqueio de clientes em: " + LocalDateTime.now());
        LocalDate tresDiasAtras = LocalDate.now().minusDays(3);

        List<Fatura> faturasCriticas = faturaRepository.findByStatusAndDataVencimentoBefore('A', tresDiasAtras);

        for (Fatura fatura : faturasCriticas) {
            Cliente cliente = fatura.getCliente();
            if (cliente != null && cliente.getStatusBloqueio() == 'A') {
                cliente.setStatusBloqueio('B');
                cliente.setLimiteCredito(BigDecimal.ZERO);
                clienteRepository.save(cliente);
                System.out.println("Cliente " + cliente.getNome() + " (CPF: " + cliente.getCpf() + ") bloqueado devido a fatura ID " + fatura.getId() + " atrasada.");
            }
        }
    }

}
