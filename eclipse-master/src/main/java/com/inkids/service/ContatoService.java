package com.inkids.service;

import com.inkids.dao.ContatoDAO;
import com.inkids.model.Contato;

import java.util.List;

/**
 * Camada de serviço para a entidade Contato.
 * Gerencia o salvamento e a listagem de mensagens de contato.
 */
public class ContatoService {
    private final ContatoDAO contatoDAO;

    public ContatoService() {
        this.contatoDAO = new ContatoDAO();
    }

    /**
     * Salva uma nova mensagem de contato.
     * @param contato O objeto Contato a ser salvo.
     * @return O objeto Contato salvo com o ID, ou null em caso de falha.
     */
    public Contato salvarMensagem(Contato contato) {
        // Validação: Garante que o email e a mensagem não são nulos.
        if (contato == null || contato.getEmail() == null || contato.getMensagem() == null ||
            contato.getEmail().trim().isEmpty() || contato.getMensagem().trim().isEmpty()) {
            System.err.println("Tentativa de salvar contato com dados inválidos.");
            return null;
        }

        int id = contatoDAO.insert(contato);
        if (id != -1) {
            contato.setId(id);
            return contato;
        }
        return null;
    }

    /**
     * Lista todas as mensagens de contato recebidas.
     * Útil para um painel administrativo.
     * @return Uma lista de objetos Contato.
     */
    public List<Contato> listarTodasMensagens() {
        return contatoDAO.getAll();
    }
    
    /**
     * Deleta uma mensagem de contato pelo seu ID.
     * @param id O ID da mensagem a ser deletada.
     * @return true se a deleção foi bem-sucedida, false caso contrário.
     */
    public boolean deletarMensagem(int id) {
        return contatoDAO.delete(id);
    }
}