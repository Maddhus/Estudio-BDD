package br.edu.estudiobdd.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historia_usuario",
        uniqueConstraints = @UniqueConstraint(name = "uk_historia_projeto_titulo",
                columnNames = {"projeto_id", "titulo"}))
public class HistoriaUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    /** "Como ..." */
    @Column(nullable = false, length = 200)
    private String papel;

    /** "Quero ..." */
    @Column(nullable = false, length = 300)
    private String objetivo;

    /** "Para ..." */
    @Column(nullable = false, length = 300)
    private String beneficio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Prioridade prioridade;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    // LAZY: só carregamos o projeto/autor quando alguém realmente os acessa.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projeto_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_historia_projeto"))
    private Projeto projeto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_historia_autor"))
    private Usuario autor;

    // Composição: cenários e sugestões nascem e morrem com a história.
    @OneToMany(mappedBy = "historia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("posicao ASC")
    private List<Cenario> cenarios = new ArrayList<>();

    @OneToMany(mappedBy = "historia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<Sugestao> sugestoes = new ArrayList<>();

    protected HistoriaUsuario() {
    }

    public HistoriaUsuario(String titulo, String papel, String objetivo, String beneficio, Prioridade prioridade) {
        this.titulo = titulo;
        this.papel = papel;
        this.objetivo = objetivo;
        this.beneficio = beneficio;
        this.prioridade = prioridade;
    }

    @PrePersist
    void aoCriar() {
        LocalDateTime agora = LocalDateTime.now();
        if (dataCriacao == null) {
            dataCriacao = agora;
        }
        if (dataAtualizacao == null) {
            dataAtualizacao = agora;
        }
    }

    @PreUpdate
    void aoAtualizar() {
        dataAtualizacao = LocalDateTime.now();
    }

    // ---- comportamento da associação (mantém os dois lados consistentes) ----

    public void adicionarCenario(Cenario cenario) {
        cenario.setHistoria(this);
        cenario.setPosicao(proximaPosicaoDeCenario());
        this.cenarios.add(cenario);
    }

    public void removerCenario(Cenario cenario) {
        this.cenarios.remove(cenario);
        int posicao = 1;
        for (Cenario c : this.cenarios) {
            c.setPosicao(posicao++);
        }
    }

    public void adicionarSugestao(Sugestao sugestao) {
        sugestao.setHistoria(this);
        this.sugestoes.add(sugestao);
    }

    public void registrarAtualizacao() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    private int proximaPosicaoDeCenario() {
        return cenarios.stream().mapToInt(Cenario::getPosicao).max().orElse(0) + 1;
    }

    // ---- getters / setters ----

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getBeneficio() { return beneficio; }
    public void setBeneficio(String beneficio) { this.beneficio = beneficio; }
    public Prioridade getPrioridade() { return prioridade; }
    public void setPrioridade(Prioridade prioridade) { this.prioridade = prioridade; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }
    public List<Cenario> getCenarios() { return cenarios; }
    public List<Sugestao> getSugestoes() { return sugestoes; }
}
