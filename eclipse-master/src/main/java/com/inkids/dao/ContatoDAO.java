package com.inkids.dao;

import com.inkids.model.Contato;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Contato.
 * Contém os métodos para realizar operações na tabela de contatos.
 */
public class ContatoDAO extends DAO {

    public ContatoDAO() {
        super();
    }

    /**
     * Insere uma nova mensagem de contato no banco de dados.
     * @param contato O objeto Contato a ser inserido.
     * @return O ID gerado para a nova mensagem, ou -1 em caso de erro.
     */
    public int insert(Contato contato) {
        String sql = "INSERT INTO Contatos (nome, email, telefone, assunto, mensagem, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        int generatedId = -1;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, contato.getNome());
                pstmt.setString(2, contato.getEmail());
                pstmt.setString(3, contato.getTelefone());
                pstmt.setString(4, contato.getAssunto());
                pstmt.setString(5, contato.getMensagem());
                pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            generatedId = rs.getInt(1);
                            contato.setId(generatedId);
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao inserir contato: " + e.getMessage());
            } finally {
                close();
            }
        }
        return generatedId;
    }

    /**
     * Busca uma mensagem de contato pelo seu ID.
     * @param id O ID da mensagem.
     * @return Um objeto Contato, ou null se não for encontrado ou em caso de erro.
     */
    public Contato get(int id) {
        String sql = "SELECT * FROM Contatos WHERE id = ?";
        Contato contato = null;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        contato = new Contato();
                        contato.setId(rs.getInt("id"));
                        contato.setNome(rs.getString("nome"));
                        contato.setEmail(rs.getString("email"));
                        contato.setTelefone(rs.getString("telefone"));
                        contato.setAssunto(rs.getString("assunto"));
                        contato.setMensagem(rs.getString("mensagem"));
                        contato.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao buscar contato: " + e.getMessage());
            } finally {
                close();
            }
        }
        return contato;
    }

    /**
     * Lista todas as mensagens de contato do banco de dados, ordenadas pela mais recente.
     * @return Uma lista de objetos Contato.
     */
    public List<Contato> getAll() {
        String sql = "SELECT * FROM Contatos ORDER BY created_at DESC";
        List<Contato> contatos = new ArrayList<>();
        if (conectar()) {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Contato contato = new Contato();
                    contato.setId(rs.getInt("id"));
                    contato.setNome(rs.getString("nome"));
                    contato.setEmail(rs.getString("email"));
                    contato.setTelefone(rs.getString("telefone"));
                    contato.setAssunto(rs.getString("assunto"));
                    contato.setMensagem(rs.getString("mensagem"));
                    contato.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    contatos.add(contato);
                }
            } catch (SQLException e) {
                System.err.println("Erro ao listar contatos: " + e.getMessage());
            } finally {
                close();
            }
        }
        return contatos;
    }
    
    /**
     * Deleta uma mensagem de contato do banco de dados pelo seu ID.
     * @param id O ID da mensagem a ser deletada.
     * @return true se a deleção for bem-sucedida, false caso contrário.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Contatos WHERE id = ?";
        boolean success = false;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                success = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Erro ao deletar contato: " + e.getMessage());
            } finally {
                close();
            }
        }
        return success;
    }
}