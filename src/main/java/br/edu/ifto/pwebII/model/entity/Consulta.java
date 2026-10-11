package br.edu.ifto.pwebII.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_consulta")
public class Consulta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id_consulta;
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "A data e o horário da consulta são obrigatórios.")
    @Column(nullable = false)
    private LocalDateTime data;

    @NotNull(message = "O valor da consulta é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor da consulta deve ser maior que zero.")
    @Digits(integer = 10, fraction = 2, message = "Informe um valor com no máximo 10 dígitos inteiros e 2 decimais.")
    @Column(nullable = false)
    private BigDecimal valor;

    @NotBlank(message = "A observação é obrigatória.")
    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
    @Column(nullable = false, length = 500)
    private String observacao;

    // Muitas consultas possuem UM paciente
    @NotNull(message = "Selecione um paciente.")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    // Muitas consultas possuem UM médico
    @NotNull(message = "Selecione um médico.")
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_medico", nullable = false)
    private Medico medico;

    public Long getId_consulta() {
        return id_consulta;
    }

    public void setId_consulta(Long id_consulta) {
        this.id_consulta = id_consulta;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }
}
