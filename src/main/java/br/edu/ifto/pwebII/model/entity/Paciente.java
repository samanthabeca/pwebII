package br.edu.ifto.pwebII.model.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tb_paciente")
public class Paciente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_paciente;
    private String nome;
    private String telefone;

    // Um paciente possui VÁRIAS consultas
    // "paciente" é o nome do atributo mapeado com name na classe Consulta
//    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas = new ArrayList<>();

    public Long getId_paciente() {
        return id_paciente;
    }

    public void setId_paciente(Long id_paciente) {
        this.id_paciente = id_paciente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
    }

}
