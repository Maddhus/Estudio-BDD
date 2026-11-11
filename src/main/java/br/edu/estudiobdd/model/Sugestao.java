package br.edu.estudiobdd.model;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "sugestao")
public class Sugestao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome do assistente de IA usado (ex.: Claude, ChatGPT, Gemini). */
    @Column(nullable = false, length = 100)
    private String ferramenta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private DecisaoSugestao decisao = DecisaoSugestao.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_rejeicao", length = 20)
    private MotivoRejeicao motivoRejeicao;

    @Column(length = 1000)
    private String justificativa;

    @Column(name = "data_registro", nullable = false, updatable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "data_decisao")
    private LocalDateTime dataDecisao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "historia_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sugestao_historia"))
    private HistoriaUsuario historia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registrada_por_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_sugestao_registrada_por"))
    private Usuario registradaPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decidida_por_id",
            foreignKey = @ForeignKey(name = "fk_sugestao_decidida_por"))
    private Usuario decididaPor;

    protected Sugestao() {
    }

    public Sugestao(String ferramenta, String prompt, String conteudo) {
        this.ferramenta = ferramenta;
        this.prompt = prompt;
        this.conteudo = conteudo;
    }

    @PrePersist
    void aoCriar() {
        if (dataRegistro == null) {
            dataRegistro = LocalDateTime.now();
        }
    }

    /** Grava o resultado da decisão. As regras (motivo obrigatório, não decidir duas vezes) ficam no serviço. */
    public void registrarDecisao(DecisaoSugestao decisao, MotivoRejeicao motivo, String justificativa, Usuario quemDecidiu) {
        this.decisao = decisao;
        this.motivoRejeicao = motivo;
        this.justificativa = justificativa;
        this.decididaPor = quemDecidiu;
        this.dataDecisao = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getFerramenta() { return ferramenta; }
    public String getPrompt() { return prompt; }
    public String getConteudo() { return conteudo; }
    public DecisaoSugestao getDecisao() { return decisao; }
    public MotivoRejeicao getMotivoRejeicao() { return motivoRejeicao; }
    public String getJustificativa() { return justificativa; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public LocalDateTime getDataDecisao() { return dataDecisao; }
    public HistoriaUsuario getHistoria() { return historia; }
    public void setHistoria(HistoriaUsuario historia) { this.historia = historia; }
    public Usuario getRegistradaPor() { return registradaPor; }
    public void setRegistradaPor(Usuario registradaPor) { this.registradaPor = registradaPor; }
    public Usuario getDecididaPor() { return decididaPor; }
}
