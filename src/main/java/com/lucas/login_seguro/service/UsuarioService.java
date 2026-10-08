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

import com.lucas.login_seguro.dto.EdicaoUsuarioDto;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.List;

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

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."));
    }

    public void editar(String id, @Valid EdicaoUsuarioDto dados) {
        Usuario usuario = buscarPorId(id);

        String email = dados.getEmail()
                .strip()
                .toLowerCase(Locale.ROOT);

        boolean emailEmUso = usuarioRepository.findByEmail(email)
                .map(outroUsuario -> !outroUsuario.getId().equals(id))
                .orElse(false);

        if (emailEmUso) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado.");
        }

        usuario.setNome(dados.getNome().strip());
        usuario.setEmail(email);
        usuario.setRole(dados.getRole());

        try {
            usuarioRepository.save(usuario);
        } catch (DuplicateKeyException exception) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado.");
        }
    }

    public void excluir(String id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }
}
