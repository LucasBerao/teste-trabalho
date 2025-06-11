package com.inkids.dao;

import com.inkids.model.Tarefa;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Tarefa.
 * Contém os métodos para realizar operações CRUD na tabela de tarefas.
 */
public class TarefaDAO extends DAO {

    public TarefaDAO() {
        super();
    }

    /**
     * Insere uma nova tarefa no banco de dados.
     * @param tarefa O objeto Tarefa a ser inserido.
     * @return O ID gerado para a nova tarefa, ou -1 em caso de erro.
     */
    public int insert(Tarefa tarefa) {
        String sql = "INSERT INTO Tarefas (titulo, descricao, status, prioridade, usuario_id, data_criacao) VALUES (?, ?, ?, ?, ?, ?)";
        int generatedId = -1;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, tarefa.getTitulo());
                pstmt.setString(2, tarefa.getDescricao());
                pstmt.setString(3, tarefa.getStatus());
                pstmt.setString(4, tarefa.getPrioridade());
                pstmt.setInt(5, tarefa.getUsuarioId());
                pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            generatedId = rs.getInt(1);
                            tarefa.setId(generatedId);
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao inserir tarefa: " + e.getMessage());
            } finally {
                close();
            }
        }
        return generatedId;
    }

    /**
     * Busca uma tarefa pelo seu ID.
     * @param id O ID da tarefa.
     * @return Um objeto Tarefa, ou null se não for encontrada ou em caso de erro.
     */
    public Tarefa get(int id) {
        String sql = "SELECT * FROM Tarefas WHERE id = ?";
        Tarefa tarefa = null;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        tarefa = new Tarefa();
                        tarefa.setId(rs.getInt("id"));
                        tarefa.setTitulo(rs.getString("titulo"));
                        tarefa.setDescricao(rs.getString("descricao"));
                        tarefa.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
                        Timestamp dataConclusao = rs.getTimestamp("data_conclusao");
                        if (dataConclusao != null) {
                            tarefa.setDataConclusao(dataConclusao.toLocalDateTime());
                        }
                        tarefa.setStatus(rs.getString("status"));
                        tarefa.setPrioridade(rs.getString("prioridade"));
                        tarefa.setUsuarioId(rs.getInt("usuario_id"));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao buscar tarefa: " + e.getMessage());
            } finally {
                close();
            }
        }
        return tarefa;
    }

    /**
     * Lista todas as tarefas do banco de dados.
     * @return Uma lista de objetos Tarefa.
     */
    public List<Tarefa> getAll() {
        String sql = "SELECT * FROM Tarefas ORDER BY data_criacao DESC";
        List<Tarefa> tarefas = new ArrayList<>();
        if (conectar()) {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Tarefa tarefa = new Tarefa();
                    tarefa.setId(rs.getInt("id"));
                    tarefa.setTitulo(rs.getString("titulo"));
                    tarefa.setDescricao(rs.getString("descricao"));
                    tarefa.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
                    Timestamp dataConclusao = rs.getTimestamp("data_conclusao");
                    if (dataConclusao != null) {
                        tarefa.setDataConclusao(dataConclusao.toLocalDateTime());
                    }
                    tarefa.setStatus(rs.getString("status"));
                    tarefa.setPrioridade(rs.getString("prioridade"));
                    tarefa.setUsuarioId(rs.getInt("usuario_id"));
                    tarefas.add(tarefa);
                }
            } catch (SQLException e) {
                System.err.println("Erro ao listar tarefas: " + e.getMessage());
            } finally {
                close();
            }
        }
        return tarefas;
    }

    /**
     * Atualiza uma tarefa existente no banco de dados.
     * @param tarefa O objeto Tarefa com os dados atualizados.
     * @return true se a atualização for bem-sucedida, false caso contrário.
     */
    public boolean update(Tarefa tarefa) {
        String sql = "UPDATE Tarefas SET titulo = ?, descricao = ?, data_conclusao = ?, status = ?, prioridade = ? WHERE id = ?";
        boolean success = false;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, tarefa.getTitulo());
                pstmt.setString(2, tarefa.getDescricao());
                pstmt.setTimestamp(3, tarefa.getDataConclusao() != null ? Timestamp.valueOf(tarefa.getDataConclusao()) : null);
                pstmt.setString(4, tarefa.getStatus());
                pstmt.setString(5, tarefa.getPrioridade());
                pstmt.setInt(6, tarefa.getId());
                
                success = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Erro ao atualizar tarefa: " + e.getMessage());
            } finally {
                close();
            }
        }
        return success;
    }

    /**
     * Deleta uma tarefa do banco de dados pelo seu ID.
     * @param id O ID da tarefa a ser deletada.
     * @return true se a deleção for bem-sucedida, false caso contrário.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM Tarefas WHERE id = ?";
        boolean success = false;
        if (conectar()) {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                success = pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Erro ao deletar tarefa: " + e.getMessage());
            } finally {
                close();
            }
        }
        return success;
    }
}