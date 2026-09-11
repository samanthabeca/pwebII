package br.edu.ifto.pwebII.model.entity;

import jakarta.persistence.Entity;

@Entity
public abstract class PessoaFisica extends Pessoa {

    private String cpf;
    private String nome;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}