package br.edu.ifto.pwebII.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

    @Entity
    @Table(name = "tb_paciente")
    public class Paciente extends PessoaFisica {

        @NotBlank(message = "O telefone é obrigatório.")
        @Pattern(regexp = "\\d{10,11}", message = "O telefone deve conter 10 ou 11 dígitos numéricos.")
        @Column(nullable = false, length = 11)
        private String telefone;

        // Um paciente possui várias consultas
        @OneToMany(mappedBy = "paciente")
        private List<Consulta> consultas = new ArrayList<>();

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
