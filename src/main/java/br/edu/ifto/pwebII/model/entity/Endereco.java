package br.edu.ifto.pwebII.model.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "tb_endereco")
public class Endereco implements Serializable {

    //TODO [Reverse Engineering] generate columns from DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id_endereco;
    private String logradouro;
    private String bairro;
    private String cep;

    @OneToOne(mappedBy="endereco")
    private Pessoa pessoa;

    public Long getId_endereco() {
        return id_endereco;
    }

    public void setId_endereco(Long id_endereco) {
        this.id_endereco = id_endereco;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}