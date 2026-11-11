package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.RegraNegocioException;
import br.edu.estudiobdd.model.DecisaoSugestao;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.MotivoRejeicao;
import br.edu.estudiobdd.model.Sugestao;
import br.edu.estudiobdd.model.TipoPasso;
import br.edu.estudiobdd.model.Usuario;
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
import static br.edu.estudiobdd.service.Fabrica.sugestao;
import static br.edu.estudiobdd.service.Fabrica.usuario;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/** TESTE DE REGRA 3: exportação bloqueada com sugestão pendente (e sem cenários); .feature correto quando permitida. */
@ExtendWith(MockitoExtension.class)
class ExportacaoServiceTest {

    @Mock
    HistoriaUsuarioRepository historiaRepository;
    @InjectMocks
    ExportacaoService exportacaoService;

    private HistoriaUsuario historiaComUmCenario(Usuario autor) {
        HistoriaUsuario historia = historia(10L, autor);
        historia.adicionarCenario(cenario("Login com credenciais válidas",
                passo(TipoPasso.DADO, "o cliente \"maria@exemplo.com\" está cadastrado"),
                passo(TipoPasso.QUANDO, "ela faz login com a senha correta"),
                passo(TipoPasso.ENTAO, "o acesso é concedido")));
        historia.getCenarios().get(0).substituirPassos(historia.getCenarios().get(0).getPassos());
        return historia;
    }

    @Test
    void exportacaoBloqueadaComSugestaoPendente() {
        Usuario ana = usuario(1L, "Ana");
        HistoriaUsuario historia = historiaComUmCenario(ana);
        sugestao(7L, historia, ana);   // nasce PENDENTE
        when(historiaRepository.findById(10L)).thenReturn(Optional.of(historia));

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> exportacaoService.exportar(10L));

        assertTrue(erro.getMessage().contains("pendente"), erro.getMessage());
    }

    @Test
    void exportacaoBloqueadaSemCenarios() {
        HistoriaUsuario vazia = historia(10L, usuario(1L, "Ana"));
        when(historiaRepository.findById(10L)).thenReturn(Optional.of(vazia));

        RegraNegocioException erro = assertThrows(RegraNegocioException.class,
                () -> exportacaoService.exportar(10L));

        assertTrue(erro.getMessage().contains("cenário"), erro.getMessage());
    }

    @Test
    void historiaSemPendenciasGeraFeatureEmPortugues() {
        Usuario ana = usuario(1L, "Ana");
        HistoriaUsuario historia = historiaComUmCenario(ana);
        Sugestao decidida = sugestao(7L, historia, ana);
        decidida.registrarDecisao(DecisaoSugestao.REJEITADA, MotivoRejeicao.GENERICA, null, ana);
        when(historiaRepository.findById(10L)).thenReturn(Optional.of(historia));

        ArquivoFeature arquivo = exportacaoService.exportar(10L);

        String esperado = String.join("\n",
                "# language: pt",
                "Funcionalidade: Login de cliente",
                "  Como cliente cadastrado",
                "  Quero acessar minha conta",
                "  Para consultar meus pedidos",
                "",
                "  Cenário: Login com credenciais válidas",
                "    Dado o cliente \"maria@exemplo.com\" está cadastrado",
                "    Quando ela faz login com a senha correta",
                "    Então o acesso é concedido",
                "");
        assertEquals(esperado, arquivo.conteudo());
        assertEquals("login-de-cliente.feature", arquivo.nomeArquivo());
    }
}
