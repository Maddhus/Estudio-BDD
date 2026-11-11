package br.edu.estudiobdd.controller;

import br.edu.estudiobdd.dto.AceitarSugestaoRequest;
import br.edu.estudiobdd.dto.RejeitarSugestaoRequest;
import br.edu.estudiobdd.dto.SugestaoRequest;
import br.edu.estudiobdd.dto.SugestaoResponse;
import br.edu.estudiobdd.model.DecisaoSugestao;
import br.edu.estudiobdd.model.Sugestao;
import br.edu.estudiobdd.service.SugestaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SugestaoController {

    private final SugestaoService sugestaoService;

    public SugestaoController(SugestaoService sugestaoService) {
        this.sugestaoService = sugestaoService;
    }

    @GetMapping("/historias/{historiaId}/sugestoes")
    public List<SugestaoResponse> listar(
            @PathVariable Long historiaId,
            @RequestParam(required = false) DecisaoSugestao decisao) {
        return sugestaoService.listarPorHistoria(historiaId, decisao).stream()
                .map(SugestaoResponse::de)
                .toList();
    }

    @PostMapping("/historias/{historiaId}/sugestoes")
    public ResponseEntity<SugestaoResponse> registrar(
            @PathVariable Long historiaId,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId,
            @Valid @RequestBody SugestaoRequest request) {
        Sugestao criada = sugestaoService.registrar(historiaId, usuarioId, request.paraEntidade());
        return ResponseEntity.created(URI.create("/api/sugestoes/" + criada.getId()))
                .body(SugestaoResponse.de(criada));
    }

    @GetMapping("/sugestoes/{id}")
    public SugestaoResponse buscar(@PathVariable Long id) {
        return SugestaoResponse.de(sugestaoService.buscar(id));
    }

    @PostMapping("/sugestoes/{id}/aceitar")
    public SugestaoResponse aceitar(
            @PathVariable Long id,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId,
            @Valid @RequestBody(required = false) AceitarSugestaoRequest request) {
        String justificativa = request == null ? null : request.justificativa();
        return SugestaoResponse.de(sugestaoService.aceitar(id, usuarioId, justificativa));
    }

    @PostMapping("/sugestoes/{id}/rejeitar")
    public SugestaoResponse rejeitar(
            @PathVariable Long id,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId,
            @Valid @RequestBody(required = false) RejeitarSugestaoRequest request) {
        return SugestaoResponse.de(sugestaoService.rejeitar(id, usuarioId,
                request == null ? null : request.motivo(),
                request == null ? null : request.justificativa()));
    }
}
