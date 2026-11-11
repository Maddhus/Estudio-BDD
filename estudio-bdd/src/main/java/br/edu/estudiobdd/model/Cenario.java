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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cenario",
        uniqueConstraints = @UniqueConstraint(name = "uk_cenario_historia_titulo",
                columnNames = {"historia_id", "titulo"}))
public class Cenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false)
    private int posicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrigemCenario origem = OrigemCenario.HUMANO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "historia_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cenario_historia"))
    private HistoriaUsuario historia;

    // Composição: os passos são removidos junto com o cenário e mantidos ordenados por "posicao".
    @OneToMany(mappedBy = "cenario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("posicao ASC")
    private List<Passo> passos = new ArrayList<>();

    protected Cenario() {
    }

    public Cenario(String titulo, OrigemCenario origem) {
        this.titulo = titulo;
        this.origem = origem == null ? OrigemCenario.HUMANO : origem;
    }

    /**
     * Substitui todos os passos pela lista recebida, renumerando as posições a partir de 1
     * na ordem da lista. A lista pode conter passos que já pertencem a este cenário (reordenação).
     */
    public void substituirPassos(List<Passo> novos) {
        List<Passo> copia = new ArrayList<>(novos);
        this.passos.clear();
        int posicao = 1;
        for (Passo passo : copia) {
            passo.setCenario(this);
            passo.setPosicao(posicao++);
            this.passos.add(passo);
        }
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getPosicao() { return posicao; }
    public void setPosicao(int posicao) { this.posicao = posicao; }
    public OrigemCenario getOrigem() { return origem; }
    public void setOrigem(OrigemCenario origem) { this.origem = origem; }
    public HistoriaUsuario getHistoria() { return historia; }
    public void setHistoria(HistoriaUsuario historia) { this.historia = historia; }
    public List<Passo> getPassos() { return passos; }
}
