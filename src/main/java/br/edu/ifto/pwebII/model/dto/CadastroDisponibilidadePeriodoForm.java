package br.edu.ifto.pwebII.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CadastroDisponibilidadePeriodoForm {

    @NotNull(message = "Selecione um médico.")
    private Long medicoId;

    @NotNull(message = "A data inicial é obrigatória.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataInicial;

    @NotNull(message = "A data final é obrigatória.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataFinal;

    @NotEmpty(message = "Selecione pelo menos um horário.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private List<LocalTime> horarios = new ArrayList<>();

    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public LocalDate getDataInicial() { return dataInicial; }
    public void setDataInicial(LocalDate dataInicial) { this.dataInicial = dataInicial; }
    public LocalDate getDataFinal() { return dataFinal; }
    public void setDataFinal(LocalDate dataFinal) { this.dataFinal = dataFinal; }
    public List<LocalTime> getHorarios() { return horarios; }
    public void setHorarios(List<LocalTime> horarios) { this.horarios = horarios; }
}
