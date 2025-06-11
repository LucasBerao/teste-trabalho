package com.inkids.service;

import com.inkids.dao.UsuarioDAO;
import com.inkids.model.Usuario;

import java.util.List;

/**
 * Camada de serviço para a entidade Usuario.
 * Contém a lógica de negócio e coordena as operações de CRUD.
 */
public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Cria um novo usuário.
     * Pode incluir validações ou lógica de negócio antes de salvar.
     * @param usuario O objeto Usuario a ser criado.
     * @return O objeto Usuario criado com o ID, ou null em caso de falha.
     */
    public Usuario criarUsuario(Usuario usuario) {
        // Validação de exemplo: garantir que o email não seja nulo ou vazio
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            System.err.println("Tentativa de criar usuário com email vazio.");
            return null;
        }
        
        // Em um sistema real, aqui você faria o HASH da senha antes de salvar.
        // Ex: usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        int id = usuarioDAO.insert(usuario);
        if (id != -1) {
            usuario.setId(id);
            return usuario;
        }
        return null;
    }

    /**
     * Busca um usuário pelo ID.
     * @param id O ID do usuário.
     * @return O objeto Usuario encontrado, ou null se não existir.
     */
    public Usuario buscarUsuarioPorId(int id) {
        return usuarioDAO.get(id);
    }
    
    /**
     * Busca um usuário pelo email.
     * @param email O email do usuário.
     * @return O objeto Usuario encontrado, ou null se não existir.
     */
    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioDAO.getByEmail(email);
    }

    /**
     * Lista todos os usuários cadastrados.
     * @return Uma lista de objetos Usuario.
     */
    public List<Usuario> listarTodosUsuarios() {
        return usuarioDAO.getAll();
    }

    /**
     * Atualiza os dados de um usuário.
     * @param usuario O objeto Usuario com as informações atualizadas.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    public boolean atualizarUsuario(Usuario usuario) {
        // Validação de exemplo: garantir que o ID do usuário é válido.
        if (usuario.getId() <= 0) {
            return false;
        }
        return usuarioDAO.update(usuario);
    }

    /**
     * Deleta um usuário pelo seu ID.
     * @param id O ID do usuário a ser deletado.
     * @return true se a deleção foi bem-sucedida, false caso contrário.
     */
    public boolean deletarUsuario(int id) {
        return usuarioDAO.delete(id);
    }
}