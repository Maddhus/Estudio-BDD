package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.ConflitoException;
import br.edu.estudiobdd.exception.RecursoNaoEncontradoException;
import br.edu.estudiobdd.exception.RegraNegocioException;
import br.edu.estudiobdd.model.DecisaoSugestao;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.MotivoRejeicao;
import br.edu.estudiobdd.model.Sugestao;
import br.edu.estudiobdd.model.Usuario;
import br.edu.estudiobdd.repository.HistoriaUsuarioRepository;
import br.edu.estudiobdd.repository.SugestaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regra 4: sugestões começam pendentes; a rejeição exige motivo; aceitar NÃO altera os cenários
 * (o analista faz o ajuste e pode marcar o cenário com origem SUGESTAO_IA); uma sugestão decidida
 * não pode ser alterada.
 */
@Service
@Transactional(readOnly = true)
public class SugestaoService {

    private final SugestaoRepository sugestaoRepository;
    private final HistoriaUsuarioRepository historiaRepository;
    private final UsuarioService usuarioService;

    public SugestaoService(SugestaoRepository sugestaoRepository,
                           HistoriaUsuarioRepository historiaRepository,
                           UsuarioService usuarioService) {
        this.sugestaoRepository = sugestaoRepository;
        this.historiaRepository = historiaRepository;
        this.usuarioService = usuarioService;
    }

    public Sugestao buscar(Long id) {
        return sugestaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sugestão " + id + " não encontrada."));
    }

    /** Lista as sugestões da história, opcionalmente filtradas por decisão. */
    public List<Sugestao> listarPorHistoria(Long historiaId, DecisaoSugestao decisao) {
        buscarHistoria(historiaId);
        return decisao == null
                ? sugestaoRepository.findByHistoriaIdOrderByIdAsc(historiaId)
                : sugestaoRepository.findByHistoriaIdAndDecisaoOrderByIdAsc(historiaId, decisao);
    }

    @Transactional
    public Sugestao registrar(Long historiaId, Long usuarioId, Sugestao dados) {
        Usuario usuario = usuarioService.exigirIdentificado(usuarioId);
        HistoriaUsuario historia = buscarHistoria(historiaId);

        Sugestao sugestao = new Sugestao(dados.getFerramenta().trim(), dados.getPrompt().trim(),
                dados.getConteudo().trim());
        sugestao.setRegistradaPor(usuario);
        historia.adicionarSugestao(sugestao);
        return sugestaoRepository.save(sugestao);
    }

    @Transactional
    public Sugestao aceitar(Long sugestaoId, Long usuarioId, String justificativa) {
        Usuario usuario = usuarioService.exigirIdentificado(usuarioId);
        Sugestao sugestao = buscar(sugestaoId);
        exigirPendente(sugestao);

        sugestao.registrarDecisao(DecisaoSugestao.ACEITA, null, limpar(justificativa), usuario);
        return sugestao;
    }

    @Transactional
    public Sugestao rejeitar(Long sugestaoId, Long usuarioId, MotivoRejeicao motivo, String justificativa) {
        Usuario usuario = usuarioService.exigirIdentificado(usuarioId);
        Sugestao sugestao = buscar(sugestaoId);
        exigirPendente(sugestao);
        if (motivo == null) {
            throw new RegraNegocioException(
                    "A rejeição exige um motivo (INCORRETA, GENERICA, DUPLICADA, FORA_DO_ESCOPO ou OUTRO).");
        }

        sugestao.registrarDecisao(DecisaoSugestao.REJEITADA, motivo, limpar(justificativa), usuario);
        return sugestao;
    }

    private static void exigirPendente(Sugestao sugestao) {
        if (sugestao.getDecisao() != DecisaoSugestao.PENDENTE) {
            throw new ConflitoException("A sugestão já foi decidida (" + sugestao.getDecisao()
                    + ") e não pode ser alterada.");
        }
    }

    private HistoriaUsuario buscarHistoria(Long historiaId) {
        return historiaRepository.findById(historiaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("História " + historiaId + " não encontrada."));
    }

    private static String limpar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
