package com.lucas.login_seguro.controller;

import com.lucas.login_seguro.dto.EdicaoUsuarioDto;
import com.lucas.login_seguro.model.Role;
import com.lucas.login_seguro.model.Usuario;
import com.lucas.login_seguro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "admin/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String exibirEdicao(
            @PathVariable String id,
            Model model) {

        Usuario usuario = usuarioService.buscarPorId(id);

        EdicaoUsuarioDto dados = new EdicaoUsuarioDto();
        dados.setNome(usuario.getNome());
        dados.setEmail(usuario.getEmail());
        dados.setRole(usuario.getRole());

        model.addAttribute("usuario", dados);
        model.addAttribute("usuarioId", id);
        model.addAttribute("perfis", Role.values());

        return "admin/editar-usuario";
    }

    @PostMapping("/{id}/editar")
    public String editarUsuario(
            @PathVariable String id,
            @Valid @ModelAttribute("usuario") EdicaoUsuarioDto dados,
            BindingResult resultado,
            Model model) {

        if (!resultado.hasErrors()) {
            try {
                usuarioService.editar(id, dados);
                return "redirect:/admin/usuarios?editado";
            } catch (IllegalArgumentException exception) {
                resultado.reject("edicao.erro", exception.getMessage());
            }
        }

        model.addAttribute("usuarioId", id);
        model.addAttribute("perfis", Role.values());

        return "admin/editar-usuario";
    }
    @PostMapping("/{id}/excluir")
    public String excluirUsuario(@PathVariable String id) {
        usuarioService.excluir(id);
        return "redirect:/admin/usuarios?excluido";
    }
}