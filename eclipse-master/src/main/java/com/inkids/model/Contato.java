package com.inkids.model;

import java.time.LocalDateTime;

/**
 * Representa a entidade Contato.
 * Armazena uma mensagem enviada por um visitante através do formulário de contato.
 */
public class Contato {
    private int id;
    private String nome;
    private String email;
    private String telefone;
    private String assunto;
    private String mensagem;
    private LocalDateTime createdAt;

    // Construtor padrão
    public Contato() {}

    // Construtor com parâmetros essenciais
    public Contato(String nome, String email, String telefone, String assunto, String mensagem) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.assunto = assunto;
        this.mensagem = mensagem;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Contato{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", email='" + email + '\'' +
               ", telefone='" + telefone + '\'' +
               ", assunto='" + assunto + '\'' +
               ", mensagem='" + mensagem + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}