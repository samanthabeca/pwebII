package br.edu.ifto.pwebII.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_especialidade")
public class Especialidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_especialidade;

    @NotBlank(message = "O nome da especialidade é obrigatório.")
    @Size(max = 80, message = "O nome da especialidade deve ter no máximo 80 caracteres.")
    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    @OneToMany(mappedBy = "especialidade")
    private List<Medico> medicos = new ArrayList<>();

    public Long getId_especialidade() {
        return id_especialidade;
    }

    public void setId_especialidade(Long id_especialidade) {
        this.id_especialidade = id_especialidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public void setMedicos(List<Medico> medicos) {
        this.medicos = medicos;
    }
}
