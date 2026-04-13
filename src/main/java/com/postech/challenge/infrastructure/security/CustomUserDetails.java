package com.postech.challenge.infrastructure.security;

import com.postech.challenge.infrastructure.persistence.entity.PerfilUsuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    private final UUID id;
    private final String nome;
    private final String email;
    private final String senha;
    private final PerfilUsuario perfil;

    public CustomUserDetails(UUID id, String nome, String email, String senha, PerfilUsuario perfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }
}
