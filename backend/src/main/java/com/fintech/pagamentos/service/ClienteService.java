package com.fintech.pagamentos.service;

import com.fintech.pagamentos.dto.ClienteRequestDTO;
import com.fintech.pagamentos.entity.Cliente;
import com.fintech.pagamentos.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    @Transactional
    public Cliente cadastrarCliente(ClienteRequestDTO clienteDTO) {
        // Conversão DTO para Entidade
        Cliente cliente = new Cliente();
        cliente.setNome(clienteDTO.getNome());
        cliente.setCpf(clienteDTO.getCpf());
        cliente.setDataNascimento(clienteDTO.getDataNascimento());
        cliente.setStatusBloqueio(clienteDTO.getStatusBloqueio());
        cliente.setLimiteCredito(clienteDTO.getLimiteCredito());

        if (cliente.getStatusBloqueio() == null) {
            cliente.setStatusBloqueio('A');
        }
        if (cliente.getLimiteCredito() == null) {
            cliente.setLimiteCredito(BigDecimal.ZERO);
        }
        if (cliente.getStatusBloqueio() == 'B' && cliente.getLimiteCredito().compareTo(BigDecimal.ZERO) != 0) {
            cliente.setLimiteCredito(BigDecimal.ZERO);
        }

        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> buscarClientePorId(UUID id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente atualizarCliente(UUID id, ClienteRequestDTO clienteDto) {
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setNome(clienteDto.getNome());
            cliente.setCpf(clienteDto.getCpf());
            cliente.setDataNascimento(clienteDto.getDataNascimento());
            cliente.setStatusBloqueio(clienteDto.getStatusBloqueio());
            cliente.setLimiteCredito(clienteDto.getLimiteCredito());

            if (cliente.getStatusBloqueio() == 'B' && cliente.getLimiteCredito().compareTo(BigDecimal.ZERO) != 0) {
                cliente.setLimiteCredito(BigDecimal.ZERO);
            }

            return clienteRepository.save(cliente);

        }).orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
    }

    public List<Cliente> listarClientesBloqueados() {
        return clienteRepository.findByStatusBloqueio('B');
    }

}
