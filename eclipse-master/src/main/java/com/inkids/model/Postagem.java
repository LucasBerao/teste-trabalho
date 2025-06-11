package com.inkids.model;

import java.time.LocalDateTime;

/**
 * Representa a entidade Postagem.
 * Armazena o conteúdo de uma postagem, incluindo um título, o texto,
 * o ID do autor e uma URL para uma imagem gerada por IA.
 */
public class Postagem {
    private int id;
    private String titulo;
    private String conteudo;
    private int autorId;
    private String imagemUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor padrão
    public Postagem() {}

    // Construtor com parâmetros essenciais
    public Postagem(String titulo, String conteudo, int autorId, String imagemUrl) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.autorId = autorId;
        this.imagemUrl = imagemUrl;
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public int getAutorId() {
        return autorId;
    }

    public void setAutorId(int autorId) {
        this.autorId = autorId;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
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
        return "Postagem{" +
               "id=" + id +
               ", titulo='" + titulo + '\'' +
               ", conteudo='" + conteudo + '\'' +
               ", autorId=" + autorId +
               ", imagemUrl='" + imagemUrl + '\'' +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }
}