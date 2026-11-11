package br.edu.estudiobdd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projeto",
        uniqueConstraints = @UniqueConstraint(name = "uk_projeto_nome", columnNames = "nome"))
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 500)
    private String descricao;

    /** Glossário e informações do negócio que ajudam a escrever (e a revisar) os cenários. */
    @Column(name = "contexto_dominio", columnDefinition = "TEXT")
    private String contextoDominio;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @OneToMany(mappedBy = "projeto", fetch = FetchType.LAZY)
    private List<HistoriaUsuario> historias = new ArrayList<>();

    protected Projeto() {
    }

    public Projeto(String nome, String descricao, String contextoDominio) {
        this.nome = nome;
        this.descricao = descricao;
        this.contextoDominio = contextoDominio;
    }

    @PrePersist
    void aoCriar() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getContextoDominio() { return contextoDominio; }
    public void setContextoDominio(String contextoDominio) { this.contextoDominio = contextoDominio; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public List<HistoriaUsuario> getHistorias() { return historias; }
}
