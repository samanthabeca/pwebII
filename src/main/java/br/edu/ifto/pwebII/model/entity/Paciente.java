package br.edu.ifto.pwebII.model.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

    @Entity
    @Table(name = "tb_paciente")
    public class Paciente extends PessoaFisica {

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