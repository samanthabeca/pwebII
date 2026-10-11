package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Consulta;
import br.edu.ifto.pwebII.model.entity.Medico;
import br.edu.ifto.pwebII.model.jdbc.repository.ConsultaRepository;
import br.edu.ifto.pwebII.model.jdbc.repository.MedicoRepository;
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
@RequestMapping("medico")
public class MedicoController {

    private final MedicoRepository repository;
    private final ConsultaRepository consultaRep;

    // O Spring injetará o repositório automaticamente
    @Autowired
    public MedicoController(MedicoRepository repository, ConsultaRepository consultaRep) {
        this.repository = repository;
        this.consultaRep = consultaRep;
    }

    @GetMapping("/form")
    public String form(Medico medico){
        return "medico/form";
    }

    @GetMapping("/list")
    public String listar(Model model) {
        List<Medico> medicos = repository.findAll();
        model.addAttribute("medico", medicos);
        model.addAttribute("totalMedicos", medicos.size());
        return "medico/list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Medico medico, BindingResult resultado,
                       RedirectAttributes redirectAttribute) {
        if (medico.getId() != null) {
            resultado.reject("medico.id.invalido", "Não é permitido informar um ID ao cadastrar um médico.");
        }
        validarDadosUnicos(medico, resultado);

        if (resultado.hasErrors()) {
            return "medico/form";
        }

        repository.save(medico);
        redirectAttribute.addFlashAttribute("mensagemSucesso", "Médico cadastrado com sucesso!");
        return "redirect:/medico/list";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Medico medico = repository.findById(id).orElse(null);
        if (medico == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Médico não encontrado.");
            return "redirect:/medico/list";
        }

        List<Consulta> consultasDoMedico = consultaRep.findAllByMedico_Id(id);

        if (!consultasDoMedico.isEmpty()){
            redirectAttributes.addFlashAttribute("mensagemErro",
                    "Não é possível excluir o(a) médico(a) " + medico.getNome() + ", pois ele(a) possui consultas vinculadas a ele(a).");
            return "redirect:/medico/list";
        }

        repository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Médico excluído com sucesso!");

        return "redirect:/medico/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Medico medico = repository.findById(id).orElse(null);
        if (medico == null) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Médico não encontrado.");
            return "redirect:/medico/list";
        }

        model.addAttribute("medico", medico);
        return "medico/form";
    }

    @PostMapping("/update")
    public String update(@Valid @ModelAttribute Medico medico, BindingResult resultado) {
        if (medico.getId() == null || !repository.existsById(medico.getId())) {
            resultado.reject("medico.inexistente", "O médico informado não existe mais.");
        }
        validarDadosUnicos(medico, resultado);

        if (resultado.hasErrors()) {
            return "medico/form";
        }

        repository.save(medico);
        return "redirect:/medico/list";
    }

    private void validarDadosUnicos(Medico medico, BindingResult resultado) {
        if (!resultado.hasFieldErrors("cpf") && repository.findByCpf(medico.getCpf())
                .filter(existente -> !Objects.equals(existente.getId(), medico.getId()))
                .isPresent()) {
            resultado.rejectValue("cpf", "medico.cpf.duplicado", "Já existe um médico cadastrado com este CPF.");
        }

        if (!resultado.hasFieldErrors("email") && repository.findByEmailIgnoreCase(medico.getEmail())
                .filter(existente -> !Objects.equals(existente.getId(), medico.getId()))
                .isPresent()) {
            resultado.rejectValue("email", "medico.email.duplicado", "Já existe um médico cadastrado com este e-mail.");
        }
    }
}
