package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.ConflitoException;
import br.edu.estudiobdd.exception.RegraNegocioException;
import br.edu.estudiobdd.model.DecisaoSugestao;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.MotivoRejeicao;
import br.edu.estudiobdd.model.Sugestao;
import br.edu.estudiobdd.model.Usuario;
import br.edu.estudiobdd.repository.HistoriaUsuarioRepository;
import br.edu.estudiobdd.repository.SugestaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static br.edu.estudiobdd.service.Fabrica.historia;
import static br.edu.estudiobdd.service.Fabrica.sugestao;
import static br.edu.estudiobdd.service.Fabrica.usuario;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/** TESTE DE REGRA 2: rejeição de sugestão sem motivo; e sugestão decidida não pode mudar. */
@ExtendWith(MockitoExtension.class)
class SugestaoServiceTest {

    @Mock
    SugestaoRepository sugestaoRepository;
    @Mock
    HistoriaUsuarioRepository historiaRepository;
    @Mock
    UsuarioService usuarioService;
    @InjectMocks
    SugestaoService sugestaoService;

    private Usuario ana;
    private Sugestao pendente;

    private void prepararSugestaoPendente() {
        ana = usuario(1L, "Ana");
        HistoriaUsuario historia = historia(10L, ana);
        pendente = sugestao(5L, historia, ana);
        when(usuarioService.exigirIdentificado(1L)).thenReturn(ana);
        when(sugestaoRepository.findById(5L)).thenReturn(Optional.of(pendente));
    }

    @Test
    void rejeicaoSemMotivoERecusada() {
        prepararSugestaoPendente();

        assertThrows(RegraNegocioException.class,
                () -> sugestaoService.rejeitar(5L, 1L, null, "não serve"));

        assertEquals(DecisaoSugestao.PENDENTE, pendente.getDecisao());
        assertNull(pendente.getMotivoRejeicao());
    }

    @Test
    void rejeicaoComMotivoRegistraDecisao() {
        prepararSugestaoPendente();

        Sugestao decidida = sugestaoService.rejeitar(5L, 1L, MotivoRejeicao.GENERICA, "passo vago");

        assertEquals(DecisaoSugestao.REJEITADA, decidida.getDecisao());
        assertEquals(MotivoRejeicao.GENERICA, decidida.getMotivoRejeicao());
        assertEquals(ana, decidida.getDecididaPor());
    }

    @Test
    void sugestaoJaDecididaNaoPodeSerAlterada() {
        prepararSugestaoPendente();
        sugestaoService.aceitar(5L, 1L, "boa ideia");

        assertThrows(ConflitoException.class, () -> sugestaoService.aceitar(5L, 1L, null));
        assertThrows(ConflitoException.class,
                () -> sugestaoService.rejeitar(5L, 1L, MotivoRejeicao.INCORRETA, null));
        assertEquals(DecisaoSugestao.ACEITA, pendente.getDecisao());
    }
}
