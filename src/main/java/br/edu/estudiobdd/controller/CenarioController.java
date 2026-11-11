package br.edu.estudiobdd.controller;

import br.edu.estudiobdd.dto.CenarioRequest;
import br.edu.estudiobdd.dto.CenarioResponse;
import br.edu.estudiobdd.dto.PassoRequest;
import br.edu.estudiobdd.dto.PassoResponse;
import br.edu.estudiobdd.model.Cenario;
import br.edu.estudiobdd.model.Passo;
import br.edu.estudiobdd.service.CenarioService;
import br.edu.estudiobdd.service.PassoService;
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
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CenarioController {

    private final CenarioService cenarioService;
    private final PassoService passoService;

    public CenarioController(CenarioService cenarioService, PassoService passoService) {
        this.cenarioService = cenarioService;
        this.passoService = passoService;
    }

    @GetMapping("/historias/{historiaId}/cenarios")
    public List<CenarioResponse> listar(@PathVariable Long historiaId) {
        return cenarioService.listarPorHistoria(historiaId).stream().map(CenarioResponse::de).toList();
    }

    @PostMapping("/historias/{historiaId}/cenarios")
    public ResponseEntity<CenarioResponse> criar(
            @PathVariable Long historiaId,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId,
            @Valid @RequestBody CenarioRequest request) {
        Cenario criado = cenarioService.criar(historiaId, usuarioId, request.paraEntidade());
        return ResponseEntity.created(URI.create("/api/cenarios/" + criado.getId()))
                .body(CenarioResponse.de(criado));
    }

    @GetMapping("/cenarios/{id}")
    public CenarioResponse buscar(@PathVariable Long id) {
        return CenarioResponse.de(cenarioService.buscar(id));
    }

    @PutMapping("/cenarios/{id}")
    public CenarioResponse atualizar(
            @PathVariable Long id,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId,
            @Valid @RequestBody CenarioRequest request) {
        return CenarioResponse.de(cenarioService.atualizar(id, usuarioId, request.paraEntidade()));
    }

    @DeleteMapping("/cenarios/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId) {
        cenarioService.excluir(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    /** Acrescenta um passo ao cenário (na posição informada ou no final). */
    @PostMapping("/cenarios/{id}/passos")
    public ResponseEntity<PassoResponse> adicionarPasso(
            @PathVariable Long id,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId,
            @Valid @RequestBody PassoRequest request) {
        Passo criado = passoService.adicionar(id, usuarioId, request.paraEntidade(), request.posicao());
        return ResponseEntity.created(URI.create("/api/passos/" + criado.getId()))
                .body(PassoResponse.de(criado));
    }
}
