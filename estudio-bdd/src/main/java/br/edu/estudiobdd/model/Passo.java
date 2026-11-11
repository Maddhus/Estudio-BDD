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
import jakarta.persistence.Table;

@Entity
@Table(name = "passo")
public class Passo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoPasso tipo;

    @Column(nullable = false, length = 500)
    private String texto;

    @Column(nullable = false)
    private int posicao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cenario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_passo_cenario"))
    private Cenario cenario;

    protected Passo() {
    }

    public Passo(TipoPasso tipo, String texto) {
        this.tipo = tipo;
        this.texto = texto;
    }

    public Long getId() { return id; }
    public TipoPasso getTipo() { return tipo; }
    public void setTipo(TipoPasso tipo) { this.tipo = tipo; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public int getPosicao() { return posicao; }
    public void setPosicao(int posicao) { this.posicao = posicao; }
    public Cenario getCenario() { return cenario; }
    public void setCenario(Cenario cenario) { this.cenario = cenario; }
}
