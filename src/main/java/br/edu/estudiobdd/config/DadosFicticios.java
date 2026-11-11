package br.edu.estudiobdd.config;

import br.edu.estudiobdd.model.Cenario;
import br.edu.estudiobdd.model.DecisaoSugestao;
import br.edu.estudiobdd.model.HistoriaUsuario;
import br.edu.estudiobdd.model.MotivoRejeicao;
import br.edu.estudiobdd.model.OrigemCenario;
import br.edu.estudiobdd.model.Passo;
import br.edu.estudiobdd.model.Prioridade;
import br.edu.estudiobdd.model.Projeto;
import br.edu.estudiobdd.model.Sugestao;
import br.edu.estudiobdd.model.TipoPasso;
import br.edu.estudiobdd.model.Usuario;
import br.edu.estudiobdd.repository.HistoriaUsuarioRepository;
import br.edu.estudiobdd.repository.ProjetoRepository;
import br.edu.estudiobdd.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Carrega dados FICTÍCIOS na primeira execução (banco vazio). Nas próximas subidas não faz nada,
 * o que também demonstra que os dados persistem após reiniciar a aplicação.
 * Desative com app.seed.enabled=false (variável SEED_ENABLED=false).
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DadosFicticios implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DadosFicticios.class);

    private final UsuarioRepository usuarioRepository;
    private final ProjetoRepository projetoRepository;
    private final HistoriaUsuarioRepository historiaRepository;

    public DadosFicticios(UsuarioRepository usuarioRepository,
                          ProjetoRepository projetoRepository,
                          HistoriaUsuarioRepository historiaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.projetoRepository = projetoRepository;
        this.historiaRepository = historiaRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        Usuario ana = usuarioRepository.save(new Usuario("Ana Souza", "ana@exemplo.com"));
        Usuario bruno = usuarioRepository.save(new Usuario("Bruno Lima", "bruno@exemplo.com"));
        Usuario carla = usuarioRepository.save(new Usuario("Carla Dias", "carla@exemplo.com"));

        Projeto loja = projetoRepository.save(new Projeto("Loja Virtual",
                "Venda de produtos de papelaria pela internet.",
                "Cliente: pessoa cadastrada que compra. Carrinho: itens escolhidos antes do pagamento. "
                        + "Estoque: unidades disponíveis de cada produto."));
        Projeto biblioteca = projetoRepository.save(new Projeto("Biblioteca Escolar",
                "Controle de empréstimos de livros de uma escola.",
                "Bibliotecário: funcionário que registra empréstimos. Prazo de devolução: 7 dias corridos."));

        // História 1: duas sugestões decididas (uma aceita, uma rejeitada) -> pode ser exportada.
        HistoriaUsuario login = historia(loja, ana, "Login de cliente", "cliente cadastrado",
                "acessar minha conta com e-mail e senha", "consultar meus pedidos com segurança", Prioridade.ALTA);
        login.adicionarCenario(cenario("Login com credenciais válidas", OrigemCenario.HUMANO,
                passo(TipoPasso.DADO, "o cliente \"maria@exemplo.com\" está cadastrado"),
                passo(TipoPasso.QUANDO, "ela faz login com a senha correta"),
                passo(TipoPasso.ENTAO, "o acesso é concedido"),
                passo(TipoPasso.E, "a sessão do cliente é iniciada")));
        login.adicionarCenario(cenario("Login com senha incorreta é recusado", OrigemCenario.SUGESTAO_IA,
                passo(TipoPasso.DADO, "o cliente \"maria@exemplo.com\" está cadastrado"),
                passo(TipoPasso.QUANDO, "ela faz login com a senha \"errada123\""),
                passo(TipoPasso.ENTAO, "o acesso é recusado"),
                passo(TipoPasso.E, "a mensagem informa credenciais inválidas")));
        Sugestao aceita = sugestao(ana, "Claude",
                "Amplie os cenários da história de login com casos de borda.",
                "Cenário: login com senha incorreta é recusado. Dado um cliente cadastrado, "
                        + "quando ele faz login com senha errada, então o acesso é recusado.");
        aceita.registrarDecisao(DecisaoSugestao.ACEITA, null,
                "Caso de borda que faltava; virou o segundo cenário.", ana);
        Sugestao generica = sugestao(ana, "ChatGPT",
                "Amplie os cenários da história de login com casos de borda.",
                "Dado que o sistema está funcionando corretamente.");
        generica.registrarDecisao(DecisaoSugestao.REJEITADA, MotivoRejeicao.GENERICA,
                "Passo vago, sem dado concreto.", ana);
        login.adicionarSugestao(aceita);
        login.adicionarSugestao(generica);
        historiaRepository.save(login);

        // História 2: tem uma sugestão PENDENTE -> a exportação deve ser bloqueada.
        HistoriaUsuario carrinho = historia(loja, bruno, "Carrinho de compras", "cliente",
                "adicionar produtos ao carrinho", "comprar vários itens de uma vez", Prioridade.MEDIA);
        carrinho.adicionarCenario(cenario("Adicionar produto disponível ao carrinho", OrigemCenario.HUMANO,
                passo(TipoPasso.DADO, "o produto \"Caneca\" tem 5 unidades em estoque"),
                passo(TipoPasso.QUANDO, "o cliente adiciona 2 unidades da \"Caneca\" ao carrinho"),
                passo(TipoPasso.ENTAO, "o carrinho contém 2 unidades da \"Caneca\"")));
        carrinho.adicionarSugestao(sugestao(bruno, "Gemini",
                "Quais cenários de falha faltam na história do carrinho?",
                "Cenário: adicionar mais unidades do que o estoque. Dado o produto \"Caneca\" com 5 unidades, "
                        + "quando o cliente adiciona 9 unidades, então a operação é recusada."));
        historiaRepository.save(carrinho);

        // História 3: ainda sem cenários -> a exportação deve ser bloqueada.
        historiaRepository.save(historia(loja, ana, "Busca de produtos", "cliente",
                "buscar produtos pelo nome", "encontrar rapidamente o que quero comprar", Prioridade.BAIXA));

        // História 4: outro projeto, outro autor, uma sugestão rejeitada como duplicada.
        HistoriaUsuario emprestimo = historia(biblioteca, carla, "Empréstimo de livro", "bibliotecário",
                "registrar o empréstimo de um livro", "controlar o prazo de devolução", Prioridade.ALTA);
        emprestimo.adicionarCenario(cenario("Empréstimo de livro disponível", OrigemCenario.HUMANO,
                passo(TipoPasso.DADO, "o livro \"Dom Casmurro\" está disponível"),
                passo(TipoPasso.QUANDO, "o bibliotecário registra o empréstimo para o aluno \"Pedro\""),
                passo(TipoPasso.ENTAO, "o livro fica indisponível"),
                passo(TipoPasso.E, "a devolução é prevista para 7 dias depois")));
        Sugestao duplicada = sugestao(carla, "Claude",
                "Revise os cenários da história de empréstimo.",
                "Cenário: registrar empréstimo de livro disponível, com o livro ficando indisponível.");
        duplicada.registrarDecisao(DecisaoSugestao.REJEITADA, MotivoRejeicao.DUPLICADA,
                "Já existe um cenário equivalente.", carla);
        emprestimo.adicionarSugestao(duplicada);
        historiaRepository.save(emprestimo);

        log.info("Dados fictícios carregados: 3 usuários, 2 projetos e 4 histórias.");
    }

    private HistoriaUsuario historia(Projeto projeto, Usuario autor, String titulo, String papel,
                                     String objetivo, String beneficio, Prioridade prioridade) {
        HistoriaUsuario h = new HistoriaUsuario(titulo, papel, objetivo, beneficio, prioridade);
        h.setProjeto(projeto);
        h.setAutor(autor);
        return h;
    }

    private Cenario cenario(String titulo, OrigemCenario origem, Passo... passos) {
        Cenario c = new Cenario(titulo, origem);
        c.substituirPassos(java.util.List.of(passos));
        return c;
    }

    private Passo passo(TipoPasso tipo, String texto) {
        return new Passo(tipo, texto);
    }

    private Sugestao sugestao(Usuario registradaPor, String ferramenta, String prompt, String conteudo) {
        Sugestao s = new Sugestao(ferramenta, prompt, conteudo);
        s.setRegistradaPor(registradaPor);
        return s;
    }
}
