package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.RecursoNaoEncontradoException;
import br.edu.estudiobdd.model.Cenario;
import br.edu.estudiobdd.model.Passo;
import br.edu.estudiobdd.model.Usuario;
import br.edu.estudiobdd.repository.CenarioRepository;
import br.edu.estudiobdd.repository.PassoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Operações sobre um passo isolado. Depois de cada alteração o cenário inteiro é validado de novo
 * (regra 2) e as posições são renumeradas, então a ordem fica sempre consistente.
 */
@Service
@Transactional(readOnly = true)
public class PassoService {

    private final UsuarioService usuarioService;
    private final CenarioRepository cenarioRepository;
    private final PassoRepository passoRepository;

    public PassoService(UsuarioService usuarioService,
                        CenarioRepository cenarioRepository,
                        PassoRepository passoRepository) {
        this.usuarioService = usuarioService;
        this.cenarioRepository = cenarioRepository;
        this.passoRepository = passoRepository;
    }

    public Passo buscar(Long id) {
        return passoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Passo " + id + " não encontrado."));
    }

    /** Insere o passo na posição indicada (1 = primeiro). Sem posição, vai para o final. */
    @Transactional
    public Passo adicionar(Long cenarioId, Long usuarioId, Passo novo, Integer posicao) {
        Usuario usuario = usuarioService.exigirIdentificado(usuarioId);
        Cenario cenario = cenarioRepository.findById(cenarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cenário " + cenarioId + " não encontrado."));
        RegrasAutoria.exigirAutor(cenario.getHistoria(), usuario);

        List<Passo> ordem = new ArrayList<>(cenario.getPassos());
        int indice = posicao == null ? ordem.size() : limitar(posicao - 1, 0, ordem.size());
        novo.setTexto(novo.getTexto().trim());
        ordem.add(indice, novo);

        ValidadorCenario.validar(ordem);
        cenario.substituirPassos(ordem);
        cenario.getHistoria().registrarAtualizacao();
        return novo;
    }

    /** Altera tipo e texto e, se a posição for informada, move o passo. */
    @Transactional
    public Passo atualizar(Long passoId, Long usuarioId, Passo dados, Integer posicao) {
        Usuario usuario = usuarioService.exigirIdentificado(usuarioId);
        Passo passo = buscar(passoId);
        Cenario cenario = passo.getCenario();
        RegrasAutoria.exigirAutor(cenario.getHistoria(), usuario);

        int indiceAtual = cenario.getPassos().indexOf(passo);
        List<Passo> ordem = new ArrayList<>(cenario.getPassos());
        ordem.remove(passo);
        int indice = posicao == null ? indiceAtual : limitar(posicao - 1, 0, ordem.size());
        ordem.add(indice, passo);

        // Valida uma simulação (com o novo tipo) antes de tocar no passo gerenciado.
        List<Passo> simulacao = new ArrayList<>(ordem);
        simulacao.set(indice, new Passo(dados.getTipo(), dados.getTexto()));
        ValidadorCenario.validar(simulacao);

        passo.setTipo(dados.getTipo());
        passo.setTexto(dados.getTexto().trim());
        cenario.substituirPassos(ordem);
        cenario.getHistoria().registrarAtualizacao();
        return passo;
    }

    /** Exclui o passo, desde que o cenário continue válido. */
    @Transactional
    public void excluir(Long passoId, Long usuarioId) {
        Usuario usuario = usuarioService.exigirIdentificado(usuarioId);
        Passo passo = buscar(passoId);
        Cenario cenario = passo.getCenario();
        RegrasAutoria.exigirAutor(cenario.getHistoria(), usuario);

        List<Passo> ordem = new ArrayList<>(cenario.getPassos());
        ordem.remove(passo);

        ValidadorCenario.validar(ordem);
        cenario.substituirPassos(ordem);   // o passo removido da lista é apagado por orphanRemoval
        cenario.getHistoria().registrarAtualizacao();
    }

    private static int limitar(int valor, int minimo, int maximo) {
        return Math.max(minimo, Math.min(valor, maximo));
    }
}
