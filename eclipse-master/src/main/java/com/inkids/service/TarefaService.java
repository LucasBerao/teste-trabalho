package com.inkids.service;

import com.inkids.dao.TarefaDAO;
import com.inkids.model.Tarefa;

import java.util.List;

/**
 * Camada de serviço para a entidade Tarefa.
 * Contém a lógica de negócio e coordena as operações de CRUD para tarefas.
 */
public class TarefaService {
    private final TarefaDAO tarefaDAO;

    public TarefaService() {
        this.tarefaDAO = new TarefaDAO();
    }

    /**
     * Cria uma nova tarefa.
     * @param tarefa O objeto Tarefa a ser criado.
     * @return O objeto Tarefa criado com o ID, ou null em caso de falha.
     */
    public Tarefa criarTarefa(Tarefa tarefa) {
        // Validação: Garante que a tarefa e o título não são nulos.
        if (tarefa == null || tarefa.getTitulo() == null || tarefa.getTitulo().trim().isEmpty()) {
            System.err.println("Tentativa de criar tarefa com título inválido.");
            return null;
        }
        
        // Define valores padrão se não forem fornecidos
        if (tarefa.getStatus() == null) {
            tarefa.setStatus("PENDENTE");
        }
        if (tarefa.getPrioridade() == null) {
            tarefa.setPrioridade("MEDIA");
        }

        int id = tarefaDAO.insert(tarefa);
        if (id != -1) {
            tarefa.setId(id);
            return tarefa;
        }
        return null;
    }

    /**
     * Busca uma tarefa pelo seu ID.
     * @param id O ID da tarefa.
     * @return O objeto Tarefa encontrado, ou null se não existir.
     */
    public Tarefa buscarTarefaPorId(int id) {
        return tarefaDAO.get(id);
    }

    /**
     * Lista todas as tarefas cadastradas.
     * @return Uma lista de objetos Tarefa.
     */
    public List<Tarefa> listarTodasTarefas() {
        return tarefaDAO.getAll();
    }

    /**
     * Atualiza os dados de uma tarefa.
     * @param tarefa O objeto Tarefa com as informações atualizadas.
     * @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    public boolean atualizarTarefa(Tarefa tarefa) {
        if (tarefa.getId() <= 0) {
            return false;
        }
        return tarefaDAO.update(tarefa);
    }

    /**
     * Deleta uma tarefa pelo seu ID.
     * @param id O ID da tarefa a ser deletada.
     * @return true se a deleção foi bem-sucedida, false caso contrário.
     */
    public boolean deletarTarefa(int id) {
        return tarefaDAO.delete(id);
    }
}