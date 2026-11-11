package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.ConflitoException;
import br.edu.estudiobdd.exception.RecursoNaoEncontradoException;
import br.edu.estudiobdd.exception.RegraNegocioException;
import br.edu.estudiobdd.model.DecisaoSugestao;
import br.edu.estudiobdd.model.HistoriaResumo;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.MotivoRejeicao;
import br.edu.estudiobdd.model.OrigemCenario;
import br.edu.estudiobdd.model.Prioridade;
import br.edu.estudiobdd.model.Projeto;
import br.edu.estudiobdd.model.ResumoProjeto;
import br.edu.estudiobdd.repository.CenarioRepository;
import br.edu.estudiobdd.repository.HistoriaUsuarioRepository;
import br.edu.estudiobdd.repository.ProjetoRepository;
import br.edu.estudiobdd.repository.SugestaoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final HistoriaUsuarioRepository historiaRepository;
    private final CenarioRepository cenarioRepository;
    private final SugestaoRepository sugestaoRepository;

    public ProjetoService(ProjetoRepository projetoRepository,
                          HistoriaUsuarioRepository historiaRepository,
                          CenarioRepository cenarioRepository,
                          SugestaoRepository sugestaoRepository) {
        this.projetoRepository = projetoRepository;
        this.historiaRepository = historiaRepository;
        this.cenarioRepository = cenarioRepository;
        this.sugestaoRepository = sugestaoRepository;
    }

    public List<Projeto> listar() {
        return projetoRepository.findAll(Sort.by("nome"));
    }

    public Projeto buscar(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto " + id + " não encontrado."));
    }

    @Transactional
    public Projeto criar(Projeto dados) {
        String nome = dados.getNome().trim();
        if (projetoRepository.existsByNomeIgnoreCase(nome)) {
            throw new ConflitoException("Já existe um projeto chamado \"" + nome + "\".");
        }
        return projetoRepository.save(new Projeto(nome, dados.getDescricao(), dados.getContextoDominio()));
    }

    @Transactional
    public Projeto atualizar(Long id, Projeto dados) {
        Projeto projeto = buscar(id);
        String nome = dados.getNome().trim();
        if (projetoRepository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ConflitoException("Já existe outro projeto chamado \"" + nome + "\".");
        }
        projeto.setNome(nome);
        projeto.setDescricao(dados.getDescricao());
        projeto.setContextoDominio(dados.getContextoDominio());
        return projeto;
    }

    @Transactional
    public void excluir(Long id) {
        Projeto projeto = buscar(id);
        if (historiaRepository.countByProjetoId(id) > 0) {
            throw new RegraNegocioException(
                    "O projeto possui histórias e não pode ser excluído. Exclua as histórias antes.");
        }
        projetoRepository.delete(projeto);
    }

    /** Regra 6: histórias do projeto com a quantidade de cenários e de sugestões pendentes; filtro opcional por prioridade. */
    public List<HistoriaResumo> consultarHistorias(Long projetoId, Prioridade prioridade) {
        buscar(projetoId);
        List<HistoriaUsuario> historias = prioridade == null
                ? historiaRepository.findByProjetoIdOrderByIdAsc(projetoId)
                : historiaRepository.findByProjetoIdAndPrioridadeOrderByIdAsc(projetoId, prioridade);

        Map<Long, Long> cenarios = paraMapa(cenarioRepository.contarPorHistoria(projetoId));
        Map<Long, Long> pendentes = paraMapa(
                sugestaoRepository.contarPorHistoriaEDecisao(projetoId, DecisaoSugestao.PENDENTE));

        return historias.stream()
                .map(h -> new HistoriaResumo(
                        h.getId(),
                        h.getTitulo(),
                        h.getPrioridade(),
                        h.getAutor().getNome(),
                        cenarios.getOrDefault(h.getId(), 0L),
                        pendentes.getOrDefault(h.getId(), 0L)))
                .toList();
    }

    /** Regra 7: histórias, cenários por origem, sugestões por decisão e motivos de rejeição mais frequentes. */
    public ResumoProjeto resumo(Long projetoId) {
        buscar(projetoId);

        Map<OrigemCenario, Long> porOrigem = new EnumMap<>(OrigemCenario.class);
        for (OrigemCenario origem : OrigemCenario.values()) {
            porOrigem.put(origem, 0L);
        }
        for (Object[] linha : cenarioRepository.contarPorOrigem(projetoId)) {
            porOrigem.put((OrigemCenario) linha[0], ((Number) linha[1]).longValue());
        }

        Map<DecisaoSugestao, Long> porDecisao = new EnumMap<>(DecisaoSugestao.class);
        for (DecisaoSugestao decisao : DecisaoSugestao.values()) {
            porDecisao.put(decisao, 0L);
        }
        for (Object[] linha : sugestaoRepository.contarPorDecisao(projetoId)) {
            porDecisao.put((DecisaoSugestao) linha[0], ((Number) linha[1]).longValue());
        }

        List<ResumoProjeto.MotivoFrequente> motivos = sugestaoRepository
                .contarMotivosRejeicao(projetoId, DecisaoSugestao.REJEITADA).stream()
                .map(linha -> new ResumoProjeto.MotivoFrequente(
                        (MotivoRejeicao) linha[0], ((Number) linha[1]).longValue()))
                .sorted(Comparator.comparingLong(ResumoProjeto.MotivoFrequente::quantidade).reversed()
                        .thenComparing(m -> m.motivo().name()))
                .toList();

        return new ResumoProjeto(historiaRepository.countByProjetoId(projetoId), porOrigem, porDecisao, motivos);
    }

    private static Map<Long, Long> paraMapa(List<Object[]> linhas) {
        Map<Long, Long> mapa = new HashMap<>();
        for (Object[] linha : linhas) {
            mapa.put(((Number) linha[0]).longValue(), ((Number) linha[1]).longValue());
        }
        return mapa;
    }
}
