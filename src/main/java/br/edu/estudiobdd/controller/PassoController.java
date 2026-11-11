package br.edu.estudiobdd.controller;

import br.edu.estudiobdd.dto.PassoRequest;
import br.edu.estudiobdd.dto.PassoResponse;
import br.edu.estudiobdd.service.PassoService;
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
@RequestMapping("/api/passos")
public class PassoController {

    private final PassoService passoService;

    public PassoController(PassoService passoService) {
        this.passoService = passoService;
    }

    @GetMapping("/{id}")
    public PassoResponse buscar(@PathVariable Long id) {
        return PassoResponse.de(passoService.buscar(id));
    }

    @PutMapping("/{id}")
    public PassoResponse atualizar(
            @PathVariable Long id,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId,
            @Valid @RequestBody PassoRequest request) {
        return PassoResponse.de(
                passoService.atualizar(id, usuarioId, request.paraEntidade(), request.posicao()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @RequestHeader(name = Cabecalhos.USUARIO_ID, required = false) Long usuarioId) {
        passoService.excluir(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
