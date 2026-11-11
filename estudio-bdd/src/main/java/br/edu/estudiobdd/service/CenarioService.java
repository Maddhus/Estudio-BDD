package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.ConflitoException;
import br.edu.estudiobdd.exception.RecursoNaoEncontradoException;
import br.edu.estudiobdd.model.Cenario;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.Passo;
import br.edu.estudiobdd.model.Usuario;
import br.edu.estudiobdd.repository.CenarioRepository;
import br.edu.estudiobdd.repository.HistoriaUsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CenarioService {

    private final UsuarioService usuarioService;
    private final HistoriaUsuarioRepository historiaRepository;
    private final CenarioRepository cenarioRepository;

    public CenarioService(UsuarioService usuarioService,
                          HistoriaUsuarioRepository historiaRepository,
                          CenarioRepository cenarioRepository) {
        this.usuarioService = usuarioService;
        this.historiaRepository = historiaRepository;
        this.cenarioRepository = cenarioRepository;
    }

    public Cenario buscar(Long id) {
        return cenarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cenário " + id + " não encontrado."));
    }

    public List<Cenario> listarPorHistoria(Long historiaId) {
        return new ArrayList<>(buscarHistoria(historiaId).getCenarios());
    }

    /**
     * Cria um cenário já com seus passos (na ordem da lista). Aplica as regras 2 (gramática) e 3 (autoria)
     * e a unicidade do título dentro da história.
     */
    @Transactional
    public Cenario criar(Long historiaId, Long usuarioId, Cenario dados) {
        Usuario usuario = usuarioService.exigirIdentificado(usuarioId);
        HistoriaUsuario historia = buscarHistoria(historiaId);
        RegrasAutoria.exigirAutor(historia, usuario);

        String titulo = dados.getTitulo().trim();
        List<Passo> passos = new ArrayList<>(dados.getPassos());
        ValidadorCenario.validar(passos);
        if (cenarioRepository.existsByHistoriaIdAndTituloIgnoreCase(historiaId, titulo)) {
            throw new ConflitoException("Já existe um cenário com o título \"" + titulo + "\" nesta história.");
        }

        Cenario novo = new Cenario(titulo, dados.getOrigem());
        novo.substituirPassos(passos);
        historia.adicionarCenario(novo);
        historia.registrarAtualizacao();
        return cenarioRepository.save(novo);
    }

    /** Edita título, origem e a lista completa de passos (a ordem da lista define as posições). */
    @Transactional
    public Cenario atualizar(Long cenarioId, Long usuarioId, Cenario dados) {
        Usuario usuario = usuarioService.exigirIdentificado(usuarioId);
        Cenario cenario = buscar(cenarioId);
        HistoriaUsuario historia = cenario.getHistoria();
        RegrasAutoria.exigirAutor(historia, usuario);

        String titulo = dados.getTitulo().trim();
        List<Passo> passos = new ArrayList<>(dados.getPassos());
        ValidadorCenario.validar(passos);
        if (cenarioRepository.existsByHistoriaIdAndTituloIgnoreCaseAndIdNot(historia.getId(), titulo, cenarioId)) {
            throw new ConflitoException("Já existe outro cenário com o título \"" + titulo + "\" nesta história.");
        }

        cenario.setTitulo(titulo);
        if (dados.getOrigem() != null) {
            cenario.setOrigem(dados.getOrigem());
        }
        cenario.substituirPassos(passos);
        historia.registrarAtualizacao();
        return cenario;
    }

    @Transactional
    public void excluir(Long cenarioId, Long usuarioId) {
        Usuario usuario = usuarioService.exigirIdentificado(usuarioId);
        Cenario cenario = buscar(cenarioId);
        HistoriaUsuario historia = cenario.getHistoria();
        RegrasAutoria.exigirAutor(historia, usuario);

        historia.removerCenario(cenario);   // orphanRemoval apaga o cenário e seus passos
        historia.registrarAtualizacao();
    }

    private HistoriaUsuario buscarHistoria(Long historiaId) {
        return historiaRepository.findById(historiaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("História " + historiaId + " não encontrada."));
    }
}
