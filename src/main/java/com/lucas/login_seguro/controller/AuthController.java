package com.lucas.login_seguro.controller;

import com.lucas.login_seguro.dto.CadastroUsuarioDto;
import com.lucas.login_seguro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/cadastro")
    public String exibirCadastro(Model model) {
        model.addAttribute("usuario", new CadastroUsuarioDto());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @Valid @ModelAttribute("usuario") CadastroUsuarioDto dados,
            BindingResult resultado) {

        if (resultado.hasErrors()) {
            return "cadastro";
        }

        try {
            usuarioService.cadastrar(dados);
        } catch (IllegalArgumentException exception) {
            resultado.reject("cadastro.erro", exception.getMessage());
            return "cadastro";
        }

        return "redirect:/login?cadastroSucesso";
    }
        @GetMapping("/login")
        public String exibirLogin() {
            return "login";
        }
}