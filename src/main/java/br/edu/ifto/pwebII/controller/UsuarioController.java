package br.edu.ifto.pwebII.controller;

import br.edu.ifto.pwebII.model.entity.Usuario;
import br.edu.ifto.pwebII.model.jdbc.repository.UsuarioRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioRepository repository;

    public UsuarioController(UsuarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/dados")
    public String dados(@AuthenticationPrincipal UserDetails usuarioLogado, Model model) {
        Usuario usuario = repository.findByLogin(usuarioLogado.getUsername())
                .orElseThrow();
        model.addAttribute("usuario", usuario);
        return "usuario/dados";
    }
}