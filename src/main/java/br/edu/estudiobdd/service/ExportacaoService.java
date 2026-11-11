package br.edu.estudiobdd.service;

import br.edu.estudiobdd.exception.RecursoNaoEncontradoException;
import br.edu.estudiobdd.exception.RegraNegocioException;
import br.edu.estudiobdd.model.DecisaoSugestao;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.repository.HistoriaUsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regra 5: só exporta histórias com ao menos um cenário e sem sugestões pendentes,
 * em Gherkin com "# language: pt".
 */
@Service
@Transactional(readOnly = true)
public class ExportacaoService {

    private final HistoriaUsuarioRepository historiaRepository;

    public ExportacaoService(HistoriaUsuarioRepository historiaRepository) {
        this.historiaRepository = historiaRepository;
    }

    public ArquivoFeature exportar(Long historiaId) {
        HistoriaUsuario historia = historiaRepository.findById(historiaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("História " + historiaId + " não encontrada."));

        if (historia.getCenarios().isEmpty()) {
            throw new RegraNegocioException(
                    "A história não pode ser exportada: cadastre ao menos um cenário.");
        }
        long pendentes = historia.getSugestoes().stream()
                .filter(s -> s.getDecisao() == DecisaoSugestao.PENDENTE)
                .count();
        if (pendentes > 0) {
            throw new RegraNegocioException("A história não pode ser exportada: há " + pendentes
                    + " sugestão(ões) pendente(s). Aceite ou rejeite todas antes de exportar.");
        }

        return new ArquivoFeature(GeradorGherkin.nomeDoArquivo(historia.getTitulo()),
                GeradorGherkin.gerar(historia));
    }
}
