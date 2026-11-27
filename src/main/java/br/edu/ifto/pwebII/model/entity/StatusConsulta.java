package br.edu.ifto.pwebII.model.entity;

public enum StatusConsulta {
    CRIADA("Criada"),
    CANCELADA("Cancelada"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída");

    private final String descricao;

    StatusConsulta(String descricao) {
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
