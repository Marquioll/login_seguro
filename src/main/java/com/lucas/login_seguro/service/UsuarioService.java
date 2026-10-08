package com.lucas.login_seguro.service;

import com.lucas.login_seguro.dto.CadastroUsuarioDto;
import com.lucas.login_seguro.model.Role;
import com.lucas.login_seguro.model.Usuario;
import com.lucas.login_seguro.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@Validated
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void cadastrar(@Valid CadastroUsuarioDto dados) {
        String nome = dados.getNome().strip();
        String email = dados.getEmail().strip().toLowerCase(Locale.ROOT);

        if (!dados.getSenha().equals(dados.getConfirmarSenha())) {
            throw new IllegalArgumentException(
                    "A senha e a confirmação devem ser iguais.");
        }

        if (dados.getSenha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                    "A senha deve ter no máximo 72 bytes em UTF-8.");
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(dados.getSenha()));
        usuario.setRole(Role.USUARIO);

        try {
            usuarioRepository.save(usuario);
        } catch (DuplicateKeyException exception) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado.");
        }
    }
}
