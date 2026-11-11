package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.ConflitoException;
import br.edu.estudiobdd.exception.RecursoNaoEncontradoException;
import br.edu.estudiobdd.exception.RegraNegocioException;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.Projeto;
import br.edu.estudiobdd.model.Usuario;
import br.edu.estudiobdd.repository.HistoriaUsuarioRepository;
import br.edu.estudiobdd.repository.ProjetoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class HistoriaService {

    private final HistoriaUsuarioRepository historiaRepository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioService usuarioService;

    public HistoriaService(HistoriaUsuarioRepository historiaRepository,
                           ProjetoRepository projetoRepository,
                           UsuarioService usuarioService) {
        this.historiaRepository = historiaRepository;
        this.projetoRepository = projetoRepository;
        this.usuarioService = usuarioService;
    }

    public HistoriaUsuario buscar(Long id) {
        return historiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("História " + id + " não encontrada."));
    }

    /** O autor da história é o usuário atual (regra 3). */
    @Transactional
    public HistoriaUsuario criar(Long projetoId, Long usuarioId, HistoriaUsuario dados) {
        Usuario autor = usuarioService.exigirIdentificado(usuarioId);
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto " + projetoId + " não encontrado."));

        exigirNarrativaCompleta(dados);
        String titulo = dados.getTitulo().trim();
        if (historiaRepository.existsByProjetoIdAndTituloIgnoreCase(projetoId, titulo)) {
            throw new ConflitoException("Já existe uma história com o título \"" + titulo + "\" neste projeto.");
        }

        HistoriaUsuario nova = new HistoriaUsuario(titulo, dados.getPapel().trim(),
                dados.getObjetivo().trim(), dados.getBeneficio().trim(), dados.getPrioridade());
        nova.setProjeto(projeto);
        nova.setAutor(autor);
        return historiaRepository.save(nova);
    }

    @Transactional
    public HistoriaUsuario atualizar(Long id, Long usuarioId, HistoriaUsuario dados) {
        Usuario usuario = usuarioService.exigirIdentificado(usuarioId);
        HistoriaUsuario historia = buscar(id);
        RegrasAutoria.exigirAutor(historia, usuario);

        exigirNarrativaCompleta(dados);
        String titulo = dados.getTitulo().trim();
        Long projetoId = historia.getProjeto().getId();
        if (historiaRepository.existsByProjetoIdAndTituloIgnoreCaseAndIdNot(projetoId, titulo, id)) {
            throw new ConflitoException("Já existe outra história com o título \"" + titulo + "\" neste projeto.");
        }

        historia.setTitulo(titulo);
        historia.setPapel(dados.getPapel().trim());
        historia.setObjetivo(dados.getObjetivo().trim());
        historia.setBeneficio(dados.getBeneficio().trim());
        historia.setPrioridade(dados.getPrioridade());
        historia.registrarAtualizacao();
        return historia;
    }

    /** Excluir a história remove seus cenários, passos e sugestões (composição). */
    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Usuario usuario = usuarioService.exigirIdentificado(usuarioId);
        HistoriaUsuario historia = buscar(id);
        RegrasAutoria.exigirAutor(historia, usuario);
        historiaRepository.delete(historia);
    }

    /** Regra 1: papel, objetivo e benefício (e título) são obrigatórios. */
    private static void exigirNarrativaCompleta(HistoriaUsuario dados) {
        if (vazio(dados.getTitulo()) || vazio(dados.getPapel())
                || vazio(dados.getObjetivo()) || vazio(dados.getBeneficio())) {
            throw new RegraNegocioException(
                    "A história precisa de título, papel (Como...), objetivo (quero...) e benefício (para...).");
        }
        if (dados.getPrioridade() == null) {
            throw new RegraNegocioException("A história precisa de uma prioridade (ALTA, MEDIA ou BAIXA).");
        }
    }

    private static boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }
}
