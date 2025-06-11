package com.inkids.service;

import com.inkids.dao.PostagemDAO;
import com.inkids.model.Postagem;

import java.util.List;

/**
 * Camada de serviço para a entidade Postagem.
 * Contém a lógica de negócio, incluindo a integração com o serviço de geração de imagens.
 */
public class PostagemService {
    private final PostagemDAO postagemDAO;
    private final GeminiImageService geminiImageService;

    public PostagemService() {
        this.postagemDAO = new PostagemDAO();
        this.geminiImageService = new GeminiImageService();
    }

    /**
     * Cria uma nova postagem, gerando uma imagem de IA baseada no título.
     * @param postagem O objeto Postagem a ser criado.
     * @return O objeto Postagem criado com a URL da imagem e o ID, ou null em caso de falha.
     */
    public Postagem criarPostagem(Postagem postagem) {
        // Validação: Garante que a postagem e o título não são nulos.
        if (postagem == null || postagem.getTitulo() == null || postagem.getTitulo().trim().isEmpty()) {
            System.err.println("Tentativa de criar postagem com título inválido.");
            return null;
        }

        // --- Ponto de Inteligência Artificial ---
        // Gera a URL da imagem com base no título da postagem.
        String imageUrl = geminiImageService.generateImageUrl(postagem.getTitulo());
        postagem.setImagemUrl(imageUrl);
        // -----------------------------------------

        // Insere a postagem no banco de dados.
        int id = postagemDAO.insert(postagem);
        if (id != -1) {
            postagem.setId(id);
            return postagem;
        }
        
        return null;
    }

    /**
     * Busca uma postagem pelo seu ID.
     * @param id O ID da postagem.
     * @return O objeto Postagem encontrado, ou null se não existir.
     */
    public Postagem buscarPostagemPorId(int id) {
        return postagemDAO.get(id);
    }

    /**
     * Lista todas as postagens cadastradas.
     * @return Uma lista de objetos Postagem.
     */
    public List<Postagem> listarTodasPostagens() {
        return postagemDAO.getAll();
    }
    
    /**
     * Lista todas as postagens de um usuário específico.
     * @param usuarioId O ID do usuário (autor).
     * @return Uma lista de postagens do usuário.
     */
    public List<Postagem> listarPostagensPorUsuario(int usuarioId) {
        return postagemDAO.getByUserId(usuarioId);
    }

    /**
     * Atualiza os dados de uma postagem.
     * Nota: A lógica atual não gera uma nova imagem na atualização, mas poderia ser adicionada.
     * @param postagem O objeto Postagem com as informações atualizadas.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    public boolean atualizarPostagem(Postagem postagem) {
        if (postagem.getId() <= 0) {
            return false;
        }
        return postagemDAO.update(postagem);
    }

    /**
     * Deleta uma postagem pelo seu ID.
     * @param id O ID da postagem a ser deletada.
     * @return true se a deleção foi bem-sucedida, false caso contrário.
     */
    public boolean deletarPostagem(int id) {
        return postagemDAO.delete(id);
    }
}