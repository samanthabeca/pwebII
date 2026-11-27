package br.edu.ifto.pwebII.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CadastroConsultaForm {

    @NotNull(message = "Selecione um horário disponível para encaixe.")
    private Long agendaId;

    @NotNull(message = "Selecione um paciente.")
    private Long pacienteId;

    @NotNull(message = "O valor da consulta é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor da consulta deve ser maior que zero.")
    @Digits(integer = 10, fraction = 2, message = "Informe um valor com no máximo 10 dígitos inteiros e 2 decimais.")
    private BigDecimal valor;

    @NotBlank(message = "A observação é obrigatória.")
    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres.")
    private String observacao;

    private boolean presencaConfirmada;

    @AssertTrue(message = "A consulta só pode ser criada após a confirmação de presença do paciente.")
    public boolean isPresencaConfirmada() {
        return presencaConfirmada;
    }

    public Long getAgendaId() { return agendaId; }
    public void setAgendaId(Long agendaId) { this.agendaId = agendaId; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public void setPresencaConfirmada(boolean presencaConfirmada) { this.presencaConfirmada = presencaConfirmada; }
}
