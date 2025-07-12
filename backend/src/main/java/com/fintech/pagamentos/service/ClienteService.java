package com.fintech.pagamentos.service;

import com.fintech.pagamentos.dto.ClienteRequestDTO;
import com.fintech.pagamentos.dto.ClienteResponseDTO;
import com.fintech.pagamentos.entity.Cliente;
import com.fintech.pagamentos.repository.ClienteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    public ClienteService(ClienteRepository clienteRepository, ModelMapper modelMapper) {
        this.clienteRepository = clienteRepository;
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

}
