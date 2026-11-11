package br.edu.estudiobdd.controller;

import br.edu.estudiobdd.dto.HistoriaRequest;
import br.edu.estudiobdd.dto.HistoriaResponse;
import br.edu.estudiobdd.service.HistoriaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historias")
public class HistoriaController {

    private final HistoriaService historiaService;

    public HistoriaController(HistoriaService historiaService) {
        this.historiaService = historiaService;
    }

    @GetMapping("/{id}")
    public HistoriaResponse buscar(@PathVariable Long id) {
        return HistoriaResponse.de(historiaService.buscar(id));
    }

    @PutMapping("/{id}")
    public HistoriaResponse atualizar(
            @PathVariable Long id,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId,
            @Valid @RequestBody HistoriaRequest request) {
        return HistoriaResponse.de(historiaService.atualizar(id, usuarioId, request.paraEntidade()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId) {
        historiaService.excluir(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
