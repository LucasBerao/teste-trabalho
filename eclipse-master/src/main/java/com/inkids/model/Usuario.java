package com.inkids.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa a entidade Usuário.
 * Contém os dados de um usuário do sistema.
 */
public class Usuario {
    private int id;
    private String nome;
    private String email;
    private String senha; // Em uma aplicação real, armazene apenas o hash da senha
    private LocalDate dataNascimento;
    private String genero;
    private String telefone;
    private String tipoUsuario; // Ex: "USER", "ADMIN"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor padrão
    public Usuario() {}

    // Construtor com parâmetros essenciais
    public Usuario(String nome, String email, String senha, LocalDate dataNascimento, String genero, String telefone, String tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.telefone = telefone;
        this.tipoUsuario = tipoUsuario;
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Usuario{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", email='" + email + '\'' +
               // A senha não deve ser exposta no toString por segurança
               ", dataNascimento=" + dataNascimento +
               ", genero='" + genero + '\'' +
               ", telefone='" + telefone + '\'' +
               ", tipoUsuario='" + tipoUsuario + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}