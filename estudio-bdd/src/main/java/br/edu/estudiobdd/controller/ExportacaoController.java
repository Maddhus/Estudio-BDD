package br.edu.estudiobdd.controller;

import br.edu.estudiobdd.service.ArquivoFeature;
import br.edu.estudiobdd.service.ExportacaoService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/historias")
public class ExportacaoController {

    private final ExportacaoService exportacaoService;

    public ExportacaoController(ExportacaoService exportacaoService) {
        this.exportacaoService = exportacaoService;
    }

    /** Devolve o texto .feature (Gherkin em português) da história, como anexo. */
    @GetMapping("/{id}/feature")
    public ResponseEntity<String> exportar(@PathVariable Long id) {
        ArquivoFeature arquivo = exportacaoService.exportar(id);
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(arquivo.nomeArquivo()).build().toString())
                .body(arquivo.conteudo());
    }
}
