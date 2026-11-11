package br.edu.estudiobdd.service;

import br.edu.estudiobdd.model.Cenario;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.Passo;

import java.text.Normalizer;
import java.util.Locale;

/** Gera o texto .feature (Gherkin em português) de uma história. */
public final class GeradorGherkin {

    private GeradorGherkin() {
    }

    public static String gerar(HistoriaUsuario historia) {
        StringBuilder sb = new StringBuilder();
        sb.append("# language: pt\n");
        sb.append("Funcionalidade: ").append(linha(historia.getTitulo())).append('\n');
        sb.append("  Como ").append(semPrefixo(historia.getPapel(), "como")).append('\n');
        sb.append("  Quero ").append(semPrefixo(historia.getObjetivo(), "quero")).append('\n');
        sb.append("  Para ").append(semPrefixo(historia.getBeneficio(), "para")).append('\n');

        for (Cenario cenario : historia.getCenarios()) {
            sb.append('\n');
            sb.append("  Cenário: ").append(linha(cenario.getTitulo())).append('\n');
            for (Passo passo : cenario.getPassos()) {
                sb.append("    ").append(passo.getTipo().getRotulo())
                        .append(' ').append(linha(passo.getTexto())).append('\n');
            }
        }
        return sb.toString();
    }

    /** Nome de arquivo seguro: sem acentos, minúsculo, com hífens (ex.: "Login de cliente" → login-de-cliente.feature). */
    public static String nomeDoArquivo(String titulo) {
        String semAcento = Normalizer.normalize(titulo == null ? "" : titulo, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String slug = semAcento.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        return (slug.isEmpty() ? "historia" : slug) + ".feature";
    }

    /** Texto em uma linha só (o Gherkin não aceita quebras dentro de um passo). */
    private static String linha(String texto) {
        return texto == null ? "" : texto.trim().replaceAll("\\s*\\R\\s*", " ");
    }

    /** Evita "Como Como analista" caso o usuário já tenha digitado a palavra inicial. */
    private static String semPrefixo(String texto, String prefixo) {
        String t = linha(texto);
        if (t.toLowerCase(Locale.ROOT).startsWith(prefixo + " ")) {
            return t.substring(prefixo.length() + 1).trim();
        }
        return t;
    }
}
