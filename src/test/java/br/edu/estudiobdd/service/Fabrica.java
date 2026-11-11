package br.edu.estudiobdd.service;

import br.edu.estudiobdd.model.Cenario;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.OrigemCenario;
import br.edu.estudiobdd.model.Passo;
import br.edu.estudiobdd.model.Prioridade;
import br.edu.estudiobdd.model.Projeto;
import br.edu.estudiobdd.model.Sugestao;
import br.edu.estudiobdd.model.TipoPasso;
import br.edu.estudiobdd.model.Usuario;
import org.springframework.test.util.ReflectionTestUtils;

/** Montagem de objetos de domínio para os testes (sem banco de dados). */
final class Fabrica {

    private Fabrica() {
    }

    static Usuario usuario(long id, String nome) {
        Usuario u = new Usuario(nome, nome.toLowerCase() + "@exemplo.com");
        ReflectionTestUtils.setField(u, "id", id);
        return u;
    }

    static HistoriaUsuario historia(long id, Usuario autor) {
        HistoriaUsuario h = new HistoriaUsuario("Login de cliente", "cliente cadastrado",
                "acessar minha conta", "consultar meus pedidos", Prioridade.ALTA);
        h.setProjeto(new Projeto("Loja Virtual", null, null));
        h.setAutor(autor);
        ReflectionTestUtils.setField(h, "id", id);
        return h;
    }

    static Cenario cenario(String titulo, Passo... passos) {
        Cenario c = new Cenario(titulo, OrigemCenario.HUMANO);
        for (Passo p : passos) {
            c.getPassos().add(p);
        }
        return c;
    }

    static Passo passo(TipoPasso tipo, String texto) {
        return new Passo(tipo, texto);
    }

    static Sugestao sugestao(long id, HistoriaUsuario historia, Usuario registradaPor) {
        Sugestao s = new Sugestao("Claude", "Amplie os cenários.", "Dado um cliente cadastrado...");
        s.setRegistradaPor(registradaPor);
        historia.adicionarSugestao(s);
        ReflectionTestUtils.setField(s, "id", id);
        return s;
    }
}
