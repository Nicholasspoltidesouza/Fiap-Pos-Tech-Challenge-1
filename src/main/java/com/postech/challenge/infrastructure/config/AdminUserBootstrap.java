package com.postech.challenge.infrastructure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.postech.challenge.infrastructure.persistence.entity.PerfilUsuario;
import com.postech.challenge.infrastructure.persistence.entity.UsuarioEntity;
import com.postech.challenge.infrastructure.persistence.repository.UsuarioRepository;

@Component
public class AdminUserBootstrap implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserBootstrap(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@oficina.com";
        if (usuarioRepository.existsByEmail(adminEmail)) {
            return;
        }

        UsuarioEntity admin = new UsuarioEntity();
        admin.setNome("Admin");
        admin.setEmail(adminEmail);
        admin.setSenha(passwordEncoder.encode("admin123"));
        admin.setPerfil(PerfilUsuario.ADMIN);

        usuarioRepository.save(admin);
    }
}
