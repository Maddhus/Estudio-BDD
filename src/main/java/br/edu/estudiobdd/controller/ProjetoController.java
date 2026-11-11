package br.edu.estudiobdd.controller;

import br.edu.estudiobdd.dto.HistoriaRequest;
import br.edu.estudiobdd.dto.HistoriaResponse;
import br.edu.estudiobdd.dto.HistoriaResumoResponse;
import br.edu.estudiobdd.dto.ProjetoRequest;
import br.edu.estudiobdd.dto.ProjetoResponse;
import br.edu.estudiobdd.dto.ResumoProjetoResponse;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.Prioridade;
import br.edu.estudiobdd.model.Projeto;
import br.edu.estudiobdd.service.HistoriaService;
import br.edu.estudiobdd.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    private final ProjetoService projetoService;
    private final HistoriaService historiaService;

    public ProjetoController(ProjetoService projetoService, HistoriaService historiaService) {
        this.projetoService = projetoService;
        this.historiaService = historiaService;
    }

    @GetMapping
    public List<ProjetoResponse> listar() {
        return projetoService.listar().stream().map(ProjetoResponse::de).toList();
    }

    @PostMapping
    public ResponseEntity<ProjetoResponse> criar(@Valid @RequestBody ProjetoRequest request) {
        Projeto criado = projetoService.criar(request.paraEntidade());
        return ResponseEntity.created(URI.create("/api/projetos/" + criado.getId()))
                .body(ProjetoResponse.de(criado));
    }

    @GetMapping("/{id}")
    public ProjetoResponse buscar(@PathVariable Long id) {
        return ProjetoResponse.de(projetoService.buscar(id));
    }

    @PutMapping("/{id}")
    public ProjetoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProjetoRequest request) {
        return ProjetoResponse.de(projetoService.atualizar(id, request.paraEntidade()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        projetoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /** Consulta do projeto (regra 6): histórias com total de cenários e de sugestões pendentes. */
    @GetMapping("/{id}/historias")
    public List<HistoriaResumoResponse> consultarHistorias(
            @PathVariable Long id,
            @RequestParam(required = false) Prioridade prioridade) {
        return projetoService.consultarHistorias(id, prioridade).stream()
                .map(HistoriaResumoResponse::de)
                .toList();
    }

    @PostMapping("/{id}/historias")
    public ResponseEntity<HistoriaResponse> criarHistoria(
            @PathVariable Long id,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId,
            @Valid @RequestBody HistoriaRequest request) {
        HistoriaUsuario criada = historiaService.criar(id, usuarioId, request.paraEntidade());
        return ResponseEntity.created(URI.create("/api/historias/" + criada.getId()))
                .body(HistoriaResponse.de(criada));
    }

    /** Resumo do projeto (regra 7). */
    @GetMapping("/{id}/resumo")
    public ResumoProjetoResponse resumo(@PathVariable Long id) {
        return ResumoProjetoResponse.de(projetoService.resumo(id));
    }
}
