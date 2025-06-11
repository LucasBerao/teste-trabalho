package com.inkids.dao;

import com.inkids.model.Postagem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Postagem.
 * Contém os métodos para realizar operações CRUD na tabela de postagens.
 */
public class PostagemDAO extends DAO {

    public PostagemDAO() {
        super();
    }

    /**
     * Insere uma nova postagem no banco de dados.
     * @param postagem O objeto Postagem a ser inserido.
     * @return O ID gerado para a nova postagem, ou -1 em caso de erro.
     */
    public int insert(Postagem postagem) {
        String sql = "INSERT INTO Postagens (titulo, conteudo, autor_id, imagem_url, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        int generatedId = -1;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, postagem.getTitulo());
                pstmt.setString(2, postagem.getConteudo());
                pstmt.setInt(3, postagem.getAutorId());
                pstmt.setString(4, postagem.getImagemUrl());
                pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            generatedId = rs.getInt(1);
                            postagem.setId(generatedId);
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao inserir postagem: " + e.getMessage());
            } finally {
                close();
            }
        }
        return generatedId;
    }

    /**
     * Busca uma postagem pelo seu ID.
     * @param id O ID da postagem.
     * @return Um objeto Postagem, ou null se não for encontrado ou em caso de erro.
     */
    public Postagem get(int id) {
        String sql = "SELECT * FROM Postagens WHERE id = ?";
        Postagem postagem = null;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        postagem = new Postagem();
                        postagem.setId(rs.getInt("id"));
                        postagem.setTitulo(rs.getString("titulo"));
                        postagem.setConteudo(rs.getString("conteudo"));
                        postagem.setAutorId(rs.getInt("autor_id"));
                        postagem.setImagemUrl(rs.getString("imagem_url"));
                        postagem.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        postagem.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao buscar postagem: " + e.getMessage());
            } finally {
                close();
            }
        }
        return postagem;
    }

    /**
     * Lista todas as postagens do banco de dados, ordenadas pela mais recente.
     * @return Uma lista de objetos Postagem.
     */
    public List<Postagem> getAll() {
        String sql = "SELECT * FROM Postagens ORDER BY created_at DESC";
        List<Postagem> postagens = new ArrayList<>();
        if (conectar()) {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Postagem postagem = new Postagem();
                    postagem.setId(rs.getInt("id"));
                    postagem.setTitulo(rs.getString("titulo"));
                    postagem.setConteudo(rs.getString("conteudo"));
                    postagem.setAutorId(rs.getInt("autor_id"));
                    postagem.setImagemUrl(rs.getString("imagem_url"));
                    postagem.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    postagem.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    postagens.add(postagem);
                }
            } catch (SQLException e) {
                System.err.println("Erro ao listar postagens: " + e.getMessage());
            } finally {
                close();
            }
        }
        return postagens;
    }

    /**
     * Lista todas as postagens de um usuário específico.
     * @param usuarioId O ID do usuário (autor).
     * @return Uma lista de objetos Postagem.
     */
    public List<Postagem> getByUserId(int usuarioId) {
        String sql = "SELECT * FROM Postagens WHERE autor_id = ? ORDER BY created_at DESC";
        List<Postagem> postagens = new ArrayList<>();
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, usuarioId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Postagem postagem = new Postagem();
                        postagem.setId(rs.getInt("id"));
                        postagem.setTitulo(rs.getString("titulo"));
                        postagem.setConteudo(rs.getString("conteudo"));
                        postagem.setAutorId(rs.getInt("autor_id"));
                        postagem.setImagemUrl(rs.getString("imagem_url"));
                        postagem.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                        postagem.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                        postagens.add(postagem);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao listar postagens por usuário: " + e.getMessage());
            } finally {
                close();
            }
        }
        return postagens;
    }

    /**
     * Atualiza uma postagem existente no banco de dados.
     * @param postagem O objeto Postagem com os dados atualizados.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     */
    public boolean update(Postagem postagem) {
        String sql = "UPDATE Postagens SET titulo = ?, conteudo = ?, imagem_url = ?, updated_at = ? WHERE id = ?";
        boolean success = false;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, postagem.getTitulo());
                pstmt.setString(2, postagem.getConteudo());
                pstmt.setString(3, postagem.getImagemUrl());
                pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.setInt(5, postagem.getId());
                
                success = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Erro ao atualizar postagem: " + e.getMessage());
            } finally {
                close();
            }
        }
        return success;
    }

    /**
     * Deleta uma postagem do banco de dados pelo seu ID.
     * @param id O ID da postagem a ser deletada.
     * @return true se a deleção for bem-sucedida, false caso contrário.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Postagens WHERE id = ?";
        boolean success = false;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                success = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Erro ao deletar postagem: " + e.getMessage());
            } finally {
                close();
            }
        }
        return success;
    }
}