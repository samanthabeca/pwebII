package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.dto.DefinirSenhaForm;
import br.edu.ifto.pwebII.service.CredencialAcessoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DefinirSenhaController {

    private final CredencialAcessoService credencialAcessoService;

    public DefinirSenhaController(CredencialAcessoService credencialAcessoService) {
        this.credencialAcessoService = credencialAcessoService;
    }

    @GetMapping("/definir-senha")
    public String form(@RequestParam(required = false) String token,
                       @ModelAttribute("definirSenha") DefinirSenhaForm definirSenha) {
        definirSenha.setToken(token);
        return "senha/form";
    }

    @PostMapping("/definir-senha")
    public String salvar(@Valid @ModelAttribute("definirSenha") DefinirSenhaForm definirSenha,
                         BindingResult resultado, RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            return "senha/form";
        }

        try {
            credencialAcessoService.definirSenha(definirSenha.getToken(), definirSenha.getSenha());
        } catch (IllegalArgumentException ex) {
            resultado.reject("senha.token.invalido", ex.getMessage());
            return "senha/form";
        }

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Senha definida com sucesso. Faça login para continuar.");
        return "redirect:/login";
    }
}
