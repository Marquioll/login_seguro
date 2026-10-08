package com.lucas.login_seguro.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginaController {

    @GetMapping("/inicio")
    public String exibirInicio(
            Authentication authentication,
            Model model) {

        model.addAttribute("email", authentication.getName());

        return "inicio";
    }
}
