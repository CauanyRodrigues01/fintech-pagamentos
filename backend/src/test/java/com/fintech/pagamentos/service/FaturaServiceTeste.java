package com.fintech.pagamentos.service;

import com.fintech.pagamentos.dto.FaturaPaymentRequestDTO;
import com.fintech.pagamentos.dto.FaturaResponseDTO;
import com.fintech.pagamentos.entity.Cliente;
import com.fintech.pagamentos.entity.Fatura;
import com.fintech.pagamentos.repository.FaturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Habilita o uso de Mockito para JUnit 5
class FaturaServiceTest {

    @Mock
    private FaturaRepository faturaRepository;

    @Mock
    private ModelMapper modelMapper; // Mock do ModelMapper

    @InjectMocks // Injeta os mocks nas dependências de FaturaService
    private FaturaService faturaService;

    // Dados de teste para reutilização
    private Cliente clienteTeste;
    private Fatura faturaAberta;
    private Fatura faturaPaga;
    private Fatura faturaAtrasada;
    private FaturaPaymentRequestDTO paymentRequestDTO;
    private FaturaResponseDTO faturaResponseDTOAberta;
    private FaturaResponseDTO faturaResponseDTOPaga;
    private FaturaResponseDTO faturaResponseDTOAtrasada;


    @BeforeEach
        // Executado antes de cada método de teste
    void setUp() {
        // Inicializa dados de teste
        UUID clienteId = UUID.randomUUID();
        clienteTeste = new Cliente(
                clienteId, "Cliente Teste", "11122233344", LocalDate.of(1990, 1, 1),
                'A', BigDecimal.valueOf(5000.00)
        );

        UUID faturaAbertaId = UUID.randomUUID();
        faturaAberta = new Fatura(
                faturaAbertaId, clienteTeste, LocalDate.of(2025, 12, 31),
                null, BigDecimal.valueOf(100.00), 'B'
        );

        UUID faturaPagaId = UUID.randomUUID();
        faturaPaga = new Fatura(
                faturaPagaId, clienteTeste, LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 5, 28), BigDecimal.valueOf(200.00), 'P'
        );

        UUID faturaAtrasadaId = UUID.randomUUID();
        faturaAtrasada = new Fatura(
                faturaAtrasadaId, clienteTeste, LocalDate.of(2025, 7, 1), // Data no passado
                null, BigDecimal.valueOf(150.00), 'A'
        );

        paymentRequestDTO = new FaturaPaymentRequestDTO(LocalDate.now());

        faturaResponseDTOAberta = createFaturaResponseDTO(faturaAberta);
        faturaResponseDTOPaga = createFaturaResponseDTO(faturaPaga);
        faturaResponseDTOAtrasada = createFaturaResponseDTO(faturaAtrasada);

        // Configura o ModelMapper para retornar DTOs de teste quando mapeado
        lenient().when(modelMapper.map(any(Fatura.class), eq(FaturaResponseDTO.class)))
                .thenAnswer(invocation -> {
                    Fatura sourceFatura = invocation.getArgument(0);
                    return createFaturaResponseDTO(sourceFatura);
                });
    }

    // Método auxiliar para criar FaturaResponseDTO a partir de Fatura
    private FaturaResponseDTO createFaturaResponseDTO(Fatura fatura) {
        FaturaResponseDTO dto = new FaturaResponseDTO();
        dto.setId(fatura.getId());
        dto.setClienteId(fatura.getCliente().getId());
        dto.setClienteNome(fatura.getCliente().getNome());
        dto.setDataVencimento(fatura.getDataVencimento());
        dto.setDataPagamento(fatura.getDataPagamento());
        dto.setValor(fatura.getValor());
        dto.setStatus(fatura.getStatus());
        return dto;
    }


    @Test
    @DisplayName("listarTodasFaturas - Deve retornar todas as faturas com sucesso")
    void listarTodasFaturas_DeveRetornarTodasFaturasComSucesso() {
        // GIVEN
        List<Fatura> faturas = Arrays.asList(faturaAberta, faturaPaga);
        when(faturaRepository.findAllWithCliente()).thenReturn(faturas);

        // WHEN
        List<FaturaResponseDTO> result = faturaService.listarTodasFaturas();

        // THEN
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(faturaAberta.getId(), result.get(0).getId());
        assertEquals(faturaPaga.getId(), result.get(1).getId());
        verify(faturaRepository).findAllWithCliente();
        verify(modelMapper, times(2)).map(any(Fatura.class), eq(FaturaResponseDTO.class));
    }

    @Test
    @DisplayName("listarFaturasPorClienteId - Deve retornar faturas para o cliente especificado")
    void listarFaturasPorClienteId_DeveRetornarFaturasParaClienteEspecificado() {
        // GIVEN
        UUID clienteId = clienteTeste.getId();
        List<Fatura> faturasDoCliente = Arrays.asList(faturaAberta, faturaPaga);
        when(faturaRepository.findByClienteIdWithCliente(clienteId)).thenReturn(faturasDoCliente);

        // WHEN
        List<FaturaResponseDTO> result = faturaService.listarFaturasPorClienteId(clienteId);

        // THEN
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(clienteId, result.get(0).getClienteId());
        assertEquals(clienteId, result.get(1).getClienteId());
        assertEquals("Cliente Teste", result.get(0).getClienteNome()); // Verifica o nome do cliente
        verify(faturaRepository).findByClienteIdWithCliente(clienteId);
    }

    @Test
    @DisplayName("listarFaturasPorClienteId - Deve retornar lista vazia se cliente sem faturas")
    void listarFaturasPorClienteId_DeveRetornarListaVaziaSeClienteSemFaturas() {
        // GIVEN
        UUID clienteId = UUID.randomUUID(); // Um ID que não tem faturas
        when(faturaRepository.findByClienteIdWithCliente(clienteId)).thenReturn(Collections.emptyList());

        // WHEN
        List<FaturaResponseDTO> result = faturaService.listarFaturasPorClienteId(clienteId);

        // THEN
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(faturaRepository).findByClienteIdWithCliente(clienteId);
    }

    @Test
    @DisplayName("registrarPagamento - Deve registrar pagamento com sucesso para fatura Aberta")
    void registrarPagamento_DeveRegistrarPagamentoComSucessoParaFaturaAberta() {
        // GIVEN
        UUID faturaId = faturaAberta.getId();
        // Mock do findByIdWithCliente
        when(faturaRepository.findByIdWithCliente(faturaId)).thenReturn(Optional.of(faturaAberta));
        // Mock do save para retornar a fatura atualizada
        when(faturaRepository.save(any(Fatura.class))).thenAnswer(invocation -> {
            Fatura savedFatura = invocation.getArgument(0);
            savedFatura.setId(faturaId); // Garante que o ID da fatura salva é o mesmo
            return savedFatura;
        });

        // WHEN
        FaturaResponseDTO result = faturaService.registrarPagamento(faturaId, paymentRequestDTO);

        // THEN
        assertNotNull(result);
        assertEquals(faturaId, result.getId());
        assertEquals('P', result.getStatus()); // Status deve ser 'Paga'
        assertEquals(paymentRequestDTO.getDataPagamento(), result.getDataPagamento()); // Data de pagamento deve ser a do DTO
        verify(faturaRepository).findByIdWithCliente(faturaId);
        verify(faturaRepository).save(faturaAberta); // Verifica se o save foi chamado
    }

    @Test
    @DisplayName("registrarPagamento - Deve lançar exceção se fatura já estiver paga")
    void registrarPagamento_DeveLancarExcecaoSeFaturaJaPaga() {
        // GIVEN
        UUID faturaId = faturaPaga.getId();
        when(faturaRepository.findByIdWithCliente(faturaId)).thenReturn(Optional.of(faturaPaga));

        // WHEN & THEN
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            faturaService.registrarPagamento(faturaId, paymentRequestDTO);
        });
        assertEquals("Fatura já está paga.", thrown.getMessage());
        verify(faturaRepository).findByIdWithCliente(faturaId);
        verify(faturaRepository, never()).save(any(Fatura.class)); // Garante que save não foi chamado
    }

    @Test
    @DisplayName("registrarPagamento - Deve lançar exceção se fatura não encontrada")
    void registrarPagamento_DeveLancarExcecaoSeFaturaNaoEncontrada() {
        // GIVEN
        UUID faturaId = UUID.randomUUID();
        when(faturaRepository.findByIdWithCliente(faturaId)).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            faturaService.registrarPagamento(faturaId, paymentRequestDTO);
        });
        assertEquals("Fatura não encontrada com ID: " + faturaId, thrown.getMessage());
        verify(faturaRepository).findByIdWithCliente(faturaId);
        verify(faturaRepository, never()).save(any(Fatura.class));
    }

    @Test
    @DisplayName("listarFaturasAtrasadas - Deve retornar apenas faturas com status 'A'")
    void listarFaturasAtrasadas_DeveRetornarApenasFaturasAtrasadas() {
        // GIVEN
        List<Fatura> todasFaturas = Arrays.asList(faturaAberta, faturaPaga, faturaAtrasada);
        when(faturaRepository.findByStatusWithCliente('A')).thenReturn(Arrays.asList(faturaAtrasada));

        // WHEN
        List<FaturaResponseDTO> result = faturaService.listarFaturasAtrasadas();

        // THEN
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(faturaAtrasada.getId(), result.get(0).getId());
        assertEquals('A', result.get(0).getStatus());
        verify(faturaRepository).findByStatusWithCliente('A');
    }

    @Test
    @DisplayName("listarFaturasAtrasadas - Deve retornar lista vazia se não houver faturas atrasadas")
    void listarFaturasAtrasadas_DeveRetornarListaVaziaSeNaoHouverAtrasadas() {
        // GIVEN
        when(faturaRepository.findByStatusWithCliente('A')).thenReturn(Collections.emptyList());

        // WHEN
        List<FaturaResponseDTO> result = faturaService.listarFaturasAtrasadas();

        // THEN
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(faturaRepository).findByStatusWithCliente('A');
    }
}