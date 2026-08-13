package br.edu.ifto.pwebII.model.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tb_funcionario")
public class Funcionario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    // Muitos funcionários pertencem a UM departamento
    @ManyToOne
    @JoinColumn(name = "id_departamento")
    private Departamento departamento;

    private double salario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }
}