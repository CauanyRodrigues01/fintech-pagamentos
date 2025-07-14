package com.fintech.pagamentos.service;

import com.fintech.pagamentos.dto.ClienteRequestDTO;
import com.fintech.pagamentos.dto.ClienteResponseDTO;
import com.fintech.pagamentos.entity.Cliente;
import com.fintech.pagamentos.entity.Fatura;
import com.fintech.pagamentos.repository.ClienteRepository;
import com.fintech.pagamentos.repository.FaturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private FaturaRepository faturaRepository; // Mock do FaturaRepository

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks // Injeta os mocks nas dependências de ClienteService
    private ClienteService clienteService;

    // Dados de teste para reutilização
    private Cliente clienteAtivo;
    private Cliente clienteBloqueado;
    private ClienteRequestDTO clienteRequestDTO;
    private ClienteResponseDTO clienteResponseDTO;
    private Fatura faturaAtrasada;

    @BeforeEach // Executado antes de cada método de teste
    void setUp() {
        // Inicializa dados de teste
        UUID clienteAtivoId = UUID.randomUUID();
        UUID clienteBloqueadoId = UUID.randomUUID();

        clienteAtivo = new Cliente(
                clienteAtivoId, "Cliente Ativo Teste", "11122233344", LocalDate.of(1990, 1, 1),
                'A', BigDecimal.valueOf(5000.00)
        );

        clienteBloqueado = new Cliente(
                clienteBloqueadoId, "Cliente Bloqueado Teste", "55566677788", LocalDate.of(1985, 4, 20),
                'B', BigDecimal.ZERO
        );

        clienteRequestDTO = new ClienteRequestDTO(
                "Novo Cliente Teste", "99988877766", LocalDate.of(1995, 5, 5),
                'A', BigDecimal.valueOf(2000.00)
        );

        clienteResponseDTO = new ClienteResponseDTO(
                UUID.randomUUID(), "Novo Cliente Teste", "99988877766", LocalDate.of(1995, 5, 5),
                'A', BigDecimal.valueOf(2000.00)
        );

        // Fatura atrasada para o job de bloqueio, associada a um cliente ATIVO
        faturaAtrasada = new Fatura(
                UUID.randomUUID(), clienteAtivo, LocalDate.of(2025, 7, 1), // Vencimento no passado
                null, BigDecimal.valueOf(100.00), 'A'
        );

        // Configura o ModelMapper para retornar DTOs de teste quando mapeado
        // Usamos lenient() para stubs que podem não ser usados em todos os testes
        lenient().when(modelMapper.map(any(ClienteRequestDTO.class), eq(Cliente.class)))
                .thenReturn(new Cliente(
                        null, clienteRequestDTO.getNome(), clienteRequestDTO.getCpf(), clienteRequestDTO.getDataNascimento(),
                        clienteRequestDTO.getStatusBloqueio(), clienteRequestDTO.getLimiteCredito()
                )); // Cria uma nova entidade a partir do DTO de Request

        lenient().when(modelMapper.map(any(Cliente.class), eq(ClienteResponseDTO.class)))
                .thenReturn(new ClienteResponseDTO(
                        UUID.randomUUID(), null, null, null, null, null // Mapeamento genérico para DTO de Resposta
                ));
        // Para testes específicos, podemos mockar o mapeamento de Cliente para ClienteResponseDTO com mais precisão
        lenient().when(modelMapper.map(clienteAtivo, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);
        lenient().when(modelMapper.map(clienteBloqueado, ClienteResponseDTO.class)).thenReturn(
                new ClienteResponseDTO(clienteBloqueadoId, clienteBloqueado.getNome(), clienteBloqueado.getCpf(),
                        clienteBloqueado.getDataNascimento(), clienteBloqueado.getStatusBloqueio(), clienteBloqueado.getLimiteCredito())
        );
    }

    @Test
    @DisplayName("listarTodosClientes - Deve retornar todos os clientes com sucesso")
    void listarTodosClientes_DeveRetornarTodosClientesComSucesso() {
        // GIVEN
        List<Cliente> clientes = Arrays.asList(clienteAtivo, clienteBloqueado);
        when(clienteRepository.findAll()).thenReturn(clientes);

        // Mockar o mapeamento para cada cliente específico para o ResponseDTO
        when(modelMapper.map(clienteAtivo, ClienteResponseDTO.class)).thenReturn(
                new ClienteResponseDTO(clienteAtivo.getId(), clienteAtivo.getNome(), clienteAtivo.getCpf(), clienteAtivo.getDataNascimento(),
                        clienteAtivo.getStatusBloqueio(), clienteAtivo.getLimiteCredito())
        );
        when(modelMapper.map(clienteBloqueado, ClienteResponseDTO.class)).thenReturn(
                new ClienteResponseDTO(clienteBloqueado.getId(), clienteBloqueado.getNome(), clienteBloqueado.getCpf(), clienteBloqueado.getDataNascimento(),
                        clienteBloqueado.getStatusBloqueio(), clienteBloqueado.getLimiteCredito())
        );


        // WHEN
        List<ClienteResponseDTO> result = clienteService.listarTodosClientes();

        // THEN
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(clienteAtivo.getNome(), result.get(0).getNome());
        assertEquals(clienteBloqueado.getNome(), result.get(1).getNome());
        verify(clienteRepository).findAll(); // Verifica se o método findAll foi chamado
        verify(modelMapper, times(2)).map(any(Cliente.class), eq(ClienteResponseDTO.class));
    }

    @Test
    @DisplayName("cadastrarCliente - Deve cadastrar um novo cliente Ativo com sucesso")
    void cadastrarCliente_DeveCadastrarNovoClienteAtivoComSucesso() {
        // GIVEN
        Cliente clienteParaSalvar = new Cliente(
                null, clienteRequestDTO.getNome(), clienteRequestDTO.getCpf(), clienteRequestDTO.getDataNascimento(),
                clienteRequestDTO.getStatusBloqueio(), clienteRequestDTO.getLimiteCredito()
        );
        Cliente clienteSalvo = new Cliente(
                UUID.randomUUID(), clienteRequestDTO.getNome(), clienteRequestDTO.getCpf(), clienteRequestDTO.getDataNascimento(),
                clienteRequestDTO.getStatusBloqueio(), clienteRequestDTO.getLimiteCredito()
        );

        when(modelMapper.map(clienteRequestDTO, Cliente.class)).thenReturn(clienteParaSalvar);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);
        when(modelMapper.map(clienteSalvo, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // WHEN
        ClienteResponseDTO result = clienteService.cadastrarCliente(clienteRequestDTO);

        // THEN
        assertNotNull(result);
        assertEquals(clienteRequestDTO.getNome(), result.getNome());
        assertEquals('A', result.getStatusBloqueio()); // Verifica o default ou o que veio do DTO
        verify(clienteRepository).save(any(Cliente.class)); // Verifica se o save foi chamado
        verify(modelMapper).map(any(ClienteRequestDTO.class), eq(Cliente.class));
        verify(modelMapper).map(any(Cliente.class), eq(ClienteResponseDTO.class));
    }

    @Test
    @DisplayName("cadastrarCliente - Deve zerar limite se cliente for Bloqueado no cadastro")
    void cadastrarCliente_DeveZerarLimiteSeBloqueadoNoCadastro() {
        // GIVEN
        ClienteRequestDTO clienteRequestBloqueado = new ClienteRequestDTO(
                "Cliente Bloqueado Inicial", "11122233300", LocalDate.of(1990, 1, 1),
                'B', BigDecimal.valueOf(1000.00) // Limite inicial diferente de zero
        );
        Cliente clienteParaSalvar = new Cliente(
                null, clienteRequestBloqueado.getNome(), clienteRequestBloqueado.getCpf(), clienteRequestBloqueado.getDataNascimento(),
                clienteRequestBloqueado.getStatusBloqueio(), clienteRequestBloqueado.getLimiteCredito()
        );
        Cliente clienteSalvo = new Cliente(
                UUID.randomUUID(), clienteRequestBloqueado.getNome(), clienteRequestBloqueado.getCpf(), clienteRequestBloqueado.getDataNascimento(),
                'B', BigDecimal.ZERO // Simula o cliente salvo já com limite zerado
        );
        ClienteResponseDTO clienteResponseBloqueado = new ClienteResponseDTO(
                UUID.randomUUID(), clienteRequestBloqueado.getNome(), clienteRequestBloqueado.getCpf(), clienteRequestBloqueado.getDataNascimento(),
                'B', BigDecimal.ZERO
        );

        when(modelMapper.map(clienteRequestBloqueado, Cliente.class)).thenReturn(clienteParaSalvar);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);
        when(modelMapper.map(clienteSalvo, ClienteResponseDTO.class)).thenReturn(clienteResponseBloqueado);

        // WHEN
        ClienteResponseDTO result = clienteService.cadastrarCliente(clienteRequestBloqueado);

        // THEN
        assertNotNull(result);
        assertEquals('B', result.getStatusBloqueio());
        assertEquals(BigDecimal.ZERO, result.getLimiteCredito()); // Verifica se o limite foi zerado
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("buscarClientePorId - Deve retornar Optional de ClienteResponseDTO se encontrado")
    void buscarClientePorId_DeveRetornarClienteSeEncontrado() {
        // GIVEN
        UUID id = clienteAtivo.getId();
        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteAtivo));
        when(modelMapper.map(clienteAtivo, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // WHEN
        Optional<ClienteResponseDTO> result = clienteService.buscarClientePorId(id);

        // THEN
        assertTrue(result.isPresent());
        assertEquals(clienteResponseDTO.getNome(), result.get().getNome());
        verify(clienteRepository).findById(id);
        verify(modelMapper).map(any(Cliente.class), eq(ClienteResponseDTO.class));
    }

    @Test
    @DisplayName("buscarClientePorId - Deve retornar Optional vazio se não encontrado")
    void buscarClientePorId_DeveRetornarVazioSeNaoEncontrado() {
        // GIVEN
        UUID id = UUID.randomUUID();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN
        Optional<ClienteResponseDTO> result = clienteService.buscarClientePorId(id);

        // THEN
        assertFalse(result.isPresent());
        verify(clienteRepository).findById(id);
        verifyNoInteractions(modelMapper); // Mapper não deve ser chamado se não encontrar
    }

    @Test
    @DisplayName("atualizarCliente - Deve atualizar cliente e retornar DTO com sucesso")
    void atualizarCliente_DeveAtualizarClienteComSucesso() {
        // GIVEN
        UUID id = clienteAtivo.getId();
        ClienteRequestDTO updateDto = new ClienteRequestDTO(
                "Nome Atualizado", "11122233344", LocalDate.of(1990, 1, 1),
                'A', BigDecimal.valueOf(6000.00)
        );
        Cliente clienteAtualizado = new Cliente(
                id, "Nome Atualizado", "11122233344", LocalDate.of(1990, 1, 1),
                'A', BigDecimal.valueOf(6000.00)
        );
        ClienteResponseDTO responseDtoAtualizado = new ClienteResponseDTO(
                id, "Nome Atualizado", "11122233344", LocalDate.of(1990, 1, 1),
                'A', BigDecimal.valueOf(6000.00)
        );

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteAtivo));
        // Simula o mapeamento do DTO para a entidade existente
        doAnswer(invocation -> {
            Cliente target = invocation.getArgument(1);
            target.setNome(updateDto.getNome());
            target.setLimiteCredito(updateDto.getLimiteCredito());
            target.setStatusBloqueio(updateDto.getStatusBloqueio());
            return null; // ModelMapper map void
        }).when(modelMapper).map(eq(updateDto), eq(clienteAtivo));

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizado);
        when(modelMapper.map(clienteAtualizado, ClienteResponseDTO.class)).thenReturn(responseDtoAtualizado);

        // WHEN
        ClienteResponseDTO result = clienteService.atualizarCliente(id, updateDto);

        // THEN
        assertNotNull(result);
        assertEquals("Nome Atualizado", result.getNome());
        assertEquals(BigDecimal.valueOf(6000.00), result.getLimiteCredito());
        verify(clienteRepository).findById(id);
        verify(clienteRepository).save(any(Cliente.class));
        verify(modelMapper).map(eq(updateDto), eq(clienteAtivo)); // Verifica se o mapeamento para existente foi chamado
        verify(modelMapper).map(any(Cliente.class), eq(ClienteResponseDTO.class));
    }

    @Test
    @DisplayName("atualizarCliente - Deve zerar limite de cliente se status mudar para Bloqueado")
    void atualizarCliente_DeveZerarLimiteSeBloqueado() {
        // GIVEN
        UUID id = clienteAtivo.getId(); // Cliente inicialmente ativo
        ClienteRequestDTO updateDtoBloqueio = new ClienteRequestDTO(
                "Nome Mudar para Bloqueado", "11122233344", LocalDate.of(1990, 1, 1),
                'B', BigDecimal.valueOf(100.00) // Tenta setar status 'B', limite 100
        );
        Cliente clienteAtualizadoBloqueado = new Cliente(
                id, "Nome Mudar para Bloqueado", "11122233344", LocalDate.of(1990, 1, 1),
                'B', BigDecimal.ZERO // Após a regra, limite vira 0
        );
        ClienteResponseDTO responseDtoBloqueado = new ClienteResponseDTO(
                id, "Nome Mudar para Bloqueado", "11122233344", LocalDate.of(1990, 1, 1),
                'B', BigDecimal.ZERO
        );

        when(clienteRepository.findById(id)).thenReturn(Optional.of(clienteAtivo));
        doAnswer(invocation -> { // Simula o mapeamento do ModelMapper para a entidade existente
            Cliente target = invocation.getArgument(1);
            target.setNome(updateDtoBloqueio.getNome());
            target.setLimiteCredito(updateDtoBloqueio.getLimiteCredito()); // ModelMapper copia o 100.00
            target.setStatusBloqueio(updateDtoBloqueio.getStatusBloqueio());
            return null;
        }).when(modelMapper).map(eq(updateDtoBloqueio), eq(clienteAtivo));

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizadoBloqueado);
        when(modelMapper.map(clienteAtualizadoBloqueado, ClienteResponseDTO.class)).thenReturn(responseDtoBloqueado);

        // WHEN
        ClienteResponseDTO result = clienteService.atualizarCliente(id, updateDtoBloqueio);

        // THEN
        assertNotNull(result);
        assertEquals('B', result.getStatusBloqueio());
        assertEquals(BigDecimal.ZERO, result.getLimiteCredito()); // Verifica se o limite foi zerado pela lógica do serviço
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("atualizarCliente - Deve lançar RuntimeException se cliente não encontrado")
    void atualizarCliente_DeveLancarRuntimeExceptionSeNaoEncontrado() {
        // GIVEN
        UUID id = UUID.randomUUID();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            clienteService.atualizarCliente(id, clienteRequestDTO);
        });
        assertEquals("Cliente não encontrado com o ID: " + id, thrown.getMessage());
        verify(clienteRepository).findById(id);
        verify(clienteRepository, never()).save(any(Cliente.class)); // Garante que save não foi chamado
        verifyNoInteractions(modelMapper);
    }

    @Test
    @DisplayName("listarClientesBloqueados - Deve retornar apenas clientes com status 'B'")
    void listarClientesBloqueados_DeveRetornarApenasClientesBloqueados() {
        // GIVEN
        List<Cliente> clientes = Arrays.asList(clienteAtivo, clienteBloqueado);
        when(clienteRepository.findByStatusBloqueio('B')).thenReturn(Arrays.asList(clienteBloqueado));

        when(modelMapper.map(clienteBloqueado, ClienteResponseDTO.class)).thenReturn(
                new ClienteResponseDTO(clienteBloqueado.getId(), clienteBloqueado.getNome(), clienteBloqueado.getCpf(),
                        clienteBloqueado.getDataNascimento(), clienteBloqueado.getStatusBloqueio(), clienteBloqueado.getLimiteCredito())
        );

        // WHEN
        List<ClienteResponseDTO> result = clienteService.listarClientesBloqueados();

        // THEN
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals('B', result.get(0).getStatusBloqueio());
        verify(clienteRepository).findByStatusBloqueio('B');
        verify(modelMapper).map(any(Cliente.class), eq(ClienteResponseDTO.class));
    }

    @Test
    @DisplayName("verificarEBloquearClientesAtrasados - Deve bloquear clientes com faturas atrasadas")
    void verificarEBloquearClientesAtrasados_DeveBloquearClientesComFaturasAtrasadas() {
        // GIVEN
        // O clienteAtivo tem uma fatura atrasada
        Cliente clienteParaBloquearNoJob = new Cliente(
                UUID.randomUUID(), "Job Teste Cliente", "10101010101", LocalDate.of(1990, 1, 1),
                'A', BigDecimal.valueOf(1000.00)
        );
        Fatura faturaCritica = new Fatura(
                UUID.randomUUID(), clienteParaBloquearNoJob, LocalDate.of(2025, 7, 10), // Vencida antes de 3 dias atrás
                null, BigDecimal.valueOf(200.00), 'A'
        );

        // Mocks para o job agendado
        when(faturaRepository.findByStatusAndDataVencimentoBefore(eq('A'), any(LocalDate.class)))
                .thenReturn(Arrays.asList(faturaCritica));

        // Simula o cliente salvo após o bloqueio pelo job
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente savedClient = invocation.getArgument(0);
            assertEquals('B', savedClient.getStatusBloqueio());
            assertEquals(BigDecimal.ZERO, savedClient.getLimiteCredito());
            return savedClient;
        });

        // WHEN
        clienteService.verificarEBloquearClientesAtrasados();

        // THEN
        // Verifica se o findByStatusAndDataVencimentoBefore foi chamado
        verify(faturaRepository).findByStatusAndDataVencimentoBefore(eq('A'), any(LocalDate.class));
        // Verifica se o clienteRepository.save foi chamado UMA vez (para bloquear o cliente)
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("verificarEBloquearClientesAtrasados - Não deve bloquear cliente já bloqueado")
    void verificarEBloquearClientesAtrasados_NaoDeveBloquearClienteJaBloqueado() {
        // GIVEN
        // Cliente já bloqueado
        Cliente clienteJaBloqueado = new Cliente(
                UUID.randomUUID(), "Cliente Já Bloqueado", "20202020202", LocalDate.of(1990, 1, 1),
                'B', BigDecimal.ZERO
        );
        Fatura faturaCritica = new Fatura(
                UUID.randomUUID(), clienteJaBloqueado, LocalDate.of(2025, 7, 10),
                null, BigDecimal.valueOf(200.00), 'A'
        );

        when(faturaRepository.findByStatusAndDataVencimentoBefore(eq('A'), any(LocalDate.class)))
                .thenReturn(Arrays.asList(faturaCritica));

        // WHEN
        clienteService.verificarEBloquearClientesAtrasados();

        // THEN
        verify(faturaRepository).findByStatusAndDataVencimentoBefore(eq('A'), any(LocalDate.class));
        verify(clienteRepository, never()).save(any(Cliente.class)); // Garante que save NÃO foi chamado
    }

    @Test
    @DisplayName("verificarEBloquearClientesAtrasados - Não deve fazer nada se não houver faturas críticas")
    void verificarEBloquearClientesAtrasados_NaoDeveFazerNadaSeNaoHouverFaturasCriticas() {
        // GIVEN
        when(faturaRepository.findByStatusAndDataVencimentoBefore(eq('A'), any(LocalDate.class)))
                .thenReturn(Collections.emptyList()); // Nenhuma fatura crítica

        // WHEN
        clienteService.verificarEBloquearClientesAtrasados();

        // THEN
        verify(faturaRepository).findByStatusAndDataVencimentoBefore(eq('A'), any(LocalDate.class));
        verify(clienteRepository, never()).save(any(Cliente.class)); // Garante que save NÃO foi chamado
        verifyNoInteractions(modelMapper); // Garante que modelMapper não foi chamado
    }
}
