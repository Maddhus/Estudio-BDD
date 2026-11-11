package br.edu.estudiobdd.dto;

import br.edu.estudiobdd.model.DecisaoSugestao;
import br.edu.estudiobdd.model.MotivoRejeicao;
import br.edu.estudiobdd.model.Sugestao;

import java.time.LocalDateTime;

public record SugestaoResponse(
        Long id,
        Long historiaId,
        String ferramenta,
        String prompt,
        String conteudo,
        DecisaoSugestao decisao,
        MotivoRejeicao motivoRejeicao,
        String justificativa,
        LocalDateTime dataRegistro,
        LocalDateTime dataDecisao,
        UsuarioResponse registradaPor,
        UsuarioResponse decididaPor) {

    public static SugestaoResponse de(Sugestao s) {
        return new SugestaoResponse(
                s.getId(),
                s.getHistoria().getId(),
                s.getFerramenta(),
                s.getPrompt(),
                s.getConteudo(),
                s.getDecisao(),
                s.getMotivoRejeicao(),
                s.getJustificativa(),
                s.getDataRegistro(),
                s.getDataDecisao(),
                UsuarioResponse.de(s.getRegistradaPor()),
                s.getDecididaPor() == null ? null : UsuarioResponse.de(s.getDecididaPor()));
    }
}
