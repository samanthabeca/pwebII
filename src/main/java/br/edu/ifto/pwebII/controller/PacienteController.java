package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Consulta;
import br.edu.ifto.pwebII.model.entity.Paciente;
import br.edu.ifto.pwebII.model.jdbc.repository.ConsultaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.PacienteRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Objects;

@Transactional
@Controller
@RequestMapping("paciente")
public class PacienteController {

    private final PacienteRepository repository;
    private final ConsultaRepository consultaRep;

    // O Spring injetará o repositório automaticamente
    @Autowired
    public PacienteController(PacienteRepository repository, ConsultaRepository consultaRep) {
        this.repository = repository;
        this.consultaRep = consultaRep;
    }

    @GetMapping("/form")
    public String form(Paciente paciente){
        return "paciente/form";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        List<Paciente> pacientes = repository.findAll();
        model.addAttribute("paciente", pacientes);
        model.addAttribute("totalPacientes", pacientes.size());
        return "paciente/list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Paciente paciente, BindingResult resultado,
                       RedirectAttributes redirectAttribute) {
        if (paciente.getId() != null) {
            resultado.reject("paciente.id.invalido", "Não é permitido informar um ID ao cadastrar um paciente.");
        }
        validarDadosUnicos(paciente, resultado);

        if (resultado.hasErrors()) {
            return "paciente/form";
        }

        repository.save(paciente);
        redirectAttribute.addFlashAttribute("mensagemSucesso", "Paciente cadastrado com sucesso!");
        return "redirect:/paciente/list";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Paciente paciente = repository.findById(id).orElse(null);
        if (paciente == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Paciente não encontrado.");
            return "redirect:/paciente/list";
        }

        List<Consulta> consultasDoPaciente = consultaRep.findAllByPaciente_Id(id);

        if (!consultasDoPaciente.isEmpty()){
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não é possível excluir o(a) paciente " + paciente.getNome() + ", pois ele(a) possui consultas vinculadas a ele(a).");
            return "redirect:/paciente/list";
        }

        repository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Paciente excluído com sucesso!");

        return "redirect:/paciente/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Paciente paciente = repository.findById(id).orElse(null);
        if (paciente == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Paciente não encontrado.");
            return "redirect:/paciente/list";
        }

        model.addAttribute("paciente", paciente);
        return "paciente/form";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute Paciente paciente, BindingResult resultado) {
        if (paciente.getId() == null || !repository.existsById(paciente.getId())) {
            resultado.reject("paciente.inexistente", "O paciente informado não existe mais.");
        }
        validarDadosUnicos(paciente, resultado);

        if (resultado.hasErrors()) {
            return "paciente/form";
        }

        repository.save(paciente);
        return "redirect:/paciente/list";
    }

    private void validarDadosUnicos(Paciente paciente, BindingResult resultado) {
        if (!resultado.hasFieldErrors("cpf") && repository.findByCpf(paciente.getCpf())
                .filter(existente -> !Objects.equals(existente.getId(), paciente.getId()))
                .isPresent()) {
            resultado.rejectValue("cpf", "paciente.cpf.duplicado", "Já existe um paciente cadastrado com este CPF.");
        }

        if (!resultado.hasFieldErrors("email") && repository.findByEmailIgnoreCase(paciente.getEmail())
                .filter(existente -> !Objects.equals(existente.getId(), paciente.getId()))
                .isPresent()) {
            resultado.rejectValue("email", "paciente.email.duplicado", "Já existe um paciente cadastrado com este e-mail.");
        }
    }
}
