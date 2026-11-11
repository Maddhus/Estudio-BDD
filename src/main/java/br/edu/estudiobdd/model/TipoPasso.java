package br.edu.estudiobdd.model;

/**
 * Tipos de passo do Gherkin. E e MAS continuam o tipo do passo anterior
 * (por isso têm ordem -1: não participam da verificação de sequência).
 */
public enum TipoPasso {
    DADO("Dado", 0),
    QUANDO("Quando", 1),
    ENTAO("Então", 2),
    E("E", -1),
    MAS("Mas", -1);

    private final String rotulo;
    private final int ordem;

    TipoPasso(String rotulo, int ordem) {
        this.rotulo = rotulo;
        this.ordem = ordem;
    }

    /** Palavra-chave em português usada na exportação .feature. */
    public String getRotulo() {
        return rotulo;
    }

    public int getOrdem() {
        return ordem;
    }

    public boolean ehContinuacao() {
        return this == E || this == MAS;
    }
}
