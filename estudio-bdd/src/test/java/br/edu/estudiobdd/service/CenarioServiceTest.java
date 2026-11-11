package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.AcessoNegadoException;
import br.edu.estudiobdd.exception.RegraNegocioException;
import br.edu.estudiobdd.model.Cenario;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.TipoPasso;
import br.edu.estudiobdd.model.Usuario;
import br.edu.estudiobdd.repository.CenarioRepository;
import br.edu.estudiobdd.repository.HistoriaUsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.edu.estudiobdd.service.Fabrica.cenario;
import static br.edu.estudiobdd.service.Fabrica.historia;
import static br.edu.estudiobdd.service.Fabrica.passo;
import static br.edu.estudiobdd.service.Fabrica.usuario;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** TESTE DE REGRA 1: cenário sem passo Então é rejeitado (e a autoria é verificada). */
@ExtendWith(MockitoExtension.class)
class CenarioServiceTest {

    @Mock
    UsuarioService usuarioService;
    @Mock
    HistoriaUsuarioRepository historiaRepository;
    @Mock
    CenarioRepository cenarioRepository;
    @InjectMocks
    CenarioService cenarioService;

    @Test
    void cenarioSemPassoEntaoERejeitado() {
        Usuario ana = usuario(1L, "Ana");
        HistoriaUsuario historia = historia(10L, ana);
        when(usuarioService.exigirIdentificado(1L)).thenReturn(ana);
        when(historiaRepository.findById(10L)).thenReturn(Optional.of(historia));

        Cenario semEntao = cenario("Cenário sem Então",
                passo(TipoPasso.DADO, "uma história cadastrada"),
                passo(TipoPasso.QUANDO, "o analista cadastra o cenário"));

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> cenarioService.criar(10L, 1L, semEntao));

        assertTrue(erro.getMessage().contains("Então"), erro.getMessage());
        verify(cenarioRepository, never()).save(any());
        assertTrue(historia.getCenarios().isEmpty(), "nada deve ser adicionado à história");
    }

    @Test
    void cenarioValidoEAssociadoAHistoriaComPassosNumerados() {
        Usuario ana = usuario(1L, "Ana");
        HistoriaUsuario historia = historia(10L, ana);
        when(usuarioService.exigirIdentificado(1L)).thenReturn(ana);
        when(historiaRepository.findById(10L)).thenReturn(Optional.of(historia));
        when(cenarioRepository.save(any(Cenario.class))).thenAnswer(i -> i.getArgument(0));

        Cenario valido = cenario("Login com senha correta",
                passo(TipoPasso.DADO, "um cliente cadastrado"),
                passo(TipoPasso.QUANDO, "ele informa a senha correta"),
                passo(TipoPasso.ENTAO, "o acesso é concedido"),
                passo(TipoPasso.E, "a sessão é iniciada"));

        Cenario salvo = cenarioService.criar(10L, 1L, valido);

        assertEquals(1, historia.getCenarios().size());
        assertEquals(1, salvo.getPosicao());
        assertEquals(4, salvo.getPassos().size());
        for (int i = 0; i < salvo.getPassos().size(); i++) {
            assertEquals(i + 1, salvo.getPassos().get(i).getPosicao());
        }
    }

    @Test
    void quemNaoEAutorNaoCriaCenario() {
        Usuario ana = usuario(1L, "Ana");
        Usuario bruno = usuario(2L, "Bruno");
        HistoriaUsuario historia = historia(10L, ana);
        when(usuarioService.exigirIdentificado(2L)).thenReturn(bruno);
        when(historiaRepository.findById(10L)).thenReturn(Optional.of(historia));

        Cenario valido = cenario("Qualquer",
                passo(TipoPasso.QUANDO, "algo acontece"),
                passo(TipoPasso.ENTAO, "algo é observado"));

        assertThrows(AcessoNegadoException.class, () -> cenarioService.criar(10L, 2L, valido));
        verify(cenarioRepository, never()).save(any());
    }
}
