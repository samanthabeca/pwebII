package br.edu.ifto.pwebII.model.entity;

public enum StatusAgenda {
    AGENDADO("Agendado"),
    DISPONIVEL("Disponível"),
    DISPONIVEL_ENCAIXE("Disponível encaixe");

    private final String descricao;

    StatusAgenda(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
