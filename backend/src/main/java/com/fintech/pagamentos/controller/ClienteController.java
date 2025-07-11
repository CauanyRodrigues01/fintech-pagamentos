package com.fintech.pagamentos.controller;
import com.fintech.pagamentos.dto.ClienteRequestDTO;
import com.fintech.pagamentos.entity.Cliente;
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
    public ResponseEntity<List<Cliente>> listarTodosClientes() {
        List<Cliente> clientes = clienteService.listarTodosClientes();
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<Cliente> cadastrarCliente(@Valid @RequestBody ClienteRequestDTO clienteDto) {
        Cliente novoCliente = clienteService.cadastrarCliente(clienteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> consultarClientePorId(@PathVariable UUID id) {
        return clienteService.buscarClientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bloqueados")
    public ResponseEntity<List<Cliente>> listarClientesBloqueados() {
        List<Cliente> clientesBloqueados = clienteService.listarClientesBloqueados();
        return ResponseEntity.ok(clientesBloqueados);
    }

}
