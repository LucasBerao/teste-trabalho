package com.inkids.dao;

import com.inkids.model.Usuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Usuario.
 * Contém os métodos para realizar operações CRUD na tabela de usuários.
 */
public class UsuarioDAO extends DAO {

    public UsuarioDAO() {
        super();
    }

    /**
     * Insere um novo usuário no banco de dados.
     * @param usuario O objeto Usuario a ser inserido.
     * @return O ID gerado para o novo usuário, ou -1 em caso de erro.
     */
    public int insert(Usuario usuario) {
        String sql = "INSERT INTO Usuarios (nome, email, senha, data_nascimento, genero, telefone, tipo_usuario, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int generatedId = -1;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, usuario.getNome());
                pstmt.setString(2, usuario.getEmail());
                pstmt.setString(3, usuario.getSenha()); // Lembre-se de usar hash em produção
                pstmt.setDate(4, usuario.getDataNascimento() != null ? Date.valueOf(usuario.getDataNascimento()) : null);
                pstmt.setString(5, usuario.getGenero());
                pstmt.setString(6, usuario.getTelefone());
                pstmt.setString(7, usuario.getTipoUsuario());
                pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            generatedId = rs.getInt(1);
                            usuario.setId(generatedId);
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao inserir usuário: " + e.getMessage());
            } finally {
                close();
            }
        }
        return generatedId;
    }

    /**
     * Busca um usuário pelo seu ID.
     * @param id O ID do usuário.
     * @return Um objeto Usuario, ou null se não for encontrado ou em caso de erro.
     */
    public Usuario get(int id) {
        String sql = "SELECT * FROM Usuarios WHERE id = ?";
        Usuario usuario = null;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        usuario = new Usuario();
                        usuario.setId(rs.getInt("id"));
                        usuario.setNome(rs.getString("nome"));
                        usuario.setEmail(rs.getString("email"));
                        usuario.setSenha(rs.getString("senha"));
                        Date dataNasc = rs.getDate("data_nascimento");
                        if (dataNasc != null) {
                            usuario.setDataNascimento(dataNasc.toLocalDate());
                        }
                        usuario.setGenero(rs.getString("genero"));
                        usuario.setTelefone(rs.getString("telefone"));
                        usuario.setTipoUsuario(rs.getString("tipo_usuario"));
                        usuario.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        usuario.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao buscar usuário: " + e.getMessage());
            } finally {
                close();
            }
        }
        return usuario;
    }

    /**
     * Lista todos os usuários do banco de dados.
     * @return Uma lista de objetos Usuario.
     */
    public List<Usuario> getAll() {
        String sql = "SELECT * FROM Usuarios ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();
        if (conectar()) {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    // Não carregar a senha em listagens por segurança
                    Date dataNasc = rs.getDate("data_nascimento");
                    if (dataNasc != null) {
                        usuario.setDataNascimento(dataNasc.toLocalDate());
                    }
                    usuario.setGenero(rs.getString("genero"));
                    usuario.setTelefone(rs.getString("telefone"));
                    usuario.setTipoUsuario(rs.getString("tipo_usuario"));
                    usuario.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    usuario.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    usuarios.add(usuario);
                }
            } catch (SQLException e) {
                System.err.println("Erro ao listar usuários: " + e.getMessage());
            } finally {
                close();
            }
        }
        return usuarios;
    }

    /**
     * Atualiza um usuário existente no banco de dados.
     * @param usuario O objeto Usuario com os dados atualizados.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     */
    public boolean update(Usuario usuario) {
        String sql = "UPDATE Usuarios SET nome = ?, email = ?, senha = ?, data_nascimento = ?, genero = ?, telefone = ?, tipo_usuario = ?, updated_at = ? WHERE id = ?";
        boolean success = false;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, usuario.getNome());
                pstmt.setString(2, usuario.getEmail());
                pstmt.setString(3, usuario.getSenha());
                pstmt.setDate(4, usuario.getDataNascimento() != null ? Date.valueOf(usuario.getDataNascimento()) : null);
                pstmt.setString(5, usuario.getGenero());
                pstmt.setString(6, usuario.getTelefone());
                pstmt.setString(7, usuario.getTipoUsuario());
                pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setInt(9, usuario.getId());
                
                success = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            } finally {
                close();
            }
        }
        return success;
    }

    /**
     * Deleta um usuário do banco de dados pelo seu ID.
     * @param id O ID do usuário a ser deletado.
     * @return true se a deleção for bem-sucedida, false caso contrário.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Usuarios WHERE id = ?";
        boolean success = false;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                success = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Erro ao deletar usuário: " + e.getMessage());
            } finally {
                close();
            }
        }
        return success;
    }
    
    /**
     * Busca um usuário pelo seu email, útil para login.
     * @param email O email do usuário.
     * @return Um objeto Usuario, ou null se não for encontrado.
     */
    public Usuario getByEmail(String email) {
        String sql = "SELECT * FROM Usuarios WHERE email = ?";
        Usuario usuario = null;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        usuario = new Usuario();
                        usuario.setId(rs.getInt("id"));
                        usuario.setNome(rs.getString("nome"));
                        usuario.setEmail(rs.getString("email"));
                        usuario.setSenha(rs.getString("senha")); // Necessário para autenticação
                        Date dataNasc = rs.getDate("data_nascimento");
                        if (dataNasc != null) {
                            usuario.setDataNascimento(dataNasc.toLocalDate());
                        }
                        usuario.setGenero(rs.getString("genero"));
                        usuario.setTelefone(rs.getString("telefone"));
                        usuario.setTipoUsuario(rs.getString("tipo_usuario"));
                        usuario.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                        usuario.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao buscar usuário por email: " + e.getMessage());
            } finally {
                close();
            }
        }
        return usuario;
    }
}