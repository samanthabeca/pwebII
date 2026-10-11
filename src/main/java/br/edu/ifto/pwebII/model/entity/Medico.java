package br.edu.ifto.pwebII.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_medico")
public class Medico extends PessoaFisica {

    @NotBlank(message = "O CRM é obrigatório.")
    @Pattern(regexp = "\\d{4,6}/[A-Z]{2}", message = "Informe o CRM no formato 1234/UF.")
    @Column(nullable = false, length = 9)
    private String crm;

    // Um médico possui várias consultas
    @OneToMany(mappedBy = "medico")
    private List<Consulta> consultas = new ArrayList<>();

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public List<Consulta> getConsultas() {
        return consultas;
    }

    public void setConsultas(List<Consulta> consultas) {
        this.consultas = consultas;
    }
}
