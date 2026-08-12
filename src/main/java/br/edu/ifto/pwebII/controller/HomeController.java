package br.edu.ifto.pwebII.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        // Retorna o nome do arquivo HTML do menu (templates/index.html)
        return "index";
    }
}