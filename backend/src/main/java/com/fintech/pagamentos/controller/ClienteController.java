package com.fintech.pagamentos.controller;
import com.fintech.pagamentos.dto.ClienteRequestDTO;
import com.fintech.pagamentos.dto.ClienteResponseDTO;
import com.fintech.pagamentos.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodosClientes() {
        List<ClienteResponseDTO> clientes = clienteService.listarTodosClientes();
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@Valid @RequestBody ClienteRequestDTO clienteDto) {
        ClienteResponseDTO novoCliente = clienteService.cadastrarCliente(clienteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> consultarClientePorId(@PathVariable UUID id) {
        return clienteService.buscarClientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizaCliente(
            @PathVariable UUID id,
            @Valid @RequestBody ClienteRequestDTO clienteDto) {

        ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteDto);

        return ResponseEntity.ok(clienteAtualizado);

    }


    @GetMapping("/bloqueados")
    public ResponseEntity<List<ClienteResponseDTO>> listarClientesBloqueados() {
        List<ClienteResponseDTO> clientesBloqueados = clienteService.listarClientesBloqueados();
        return ResponseEntity.ok(clientesBloqueados);
    }

}
