package com.inkids.controller;

import com.inkids.model.Tarefa;
import com.inkids.service.TarefaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static spark.Spark.*;

/**
 * Controller para expor as operações de CRUD da entidade Tarefa via API REST.
 */
public class TarefaController {

    private final TarefaService tarefaService;
    private final ObjectMapper objectMapper;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        setupRoutes();
    }

    private void setupRoutes() {

        // Endpoint: Criar uma nova tarefa
        post("/api/tarefas", (request, response) -> {
            response.type("application/json");
            try {
                Tarefa tarefa = objectMapper.readValue(request.body(), Tarefa.class);
                Tarefa novaTarefa = tarefaService.criarTarefa(tarefa);

                if (novaTarefa != null) {
                    response.status(201); // 201 Created
                    return objectMapper.writeValueAsString(novaTarefa);
                } else {
                    response.status(400); // 400 Bad Request
                    return "{\"error\":\"Não foi possível criar a tarefa. Verifique os dados enviados.\"}";
                }
            } catch (Exception e) {
                response.status(500);
                return "{\"error\":\"Ocorreu um erro no servidor: " + e.getMessage() + "\"}";
            }
        });

        // Endpoint: Buscar uma tarefa por ID
        get("/api/tarefas/:id", (request, response) -> {
            response.type("application/json");
            try {
                int id = Integer.parseInt(request.params(":id"));
                Tarefa tarefa = tarefaService.buscarTarefaPorId(id);

                if (tarefa != null) {
                    response.status(200); // 200 OK
                    return objectMapper.writeValueAsString(tarefa);
                } else {
                    response.status(404); // 404 Not Found
                    return "{\"error\":\"Tarefa não encontrada.\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"error\":\"ID de tarefa inválido.\"}";
            }
        });

        // Endpoint: Listar todas as tarefas
        get("/api/tarefas", (request, response) -> {
            response.type("application/json");
            response.status(200);
            return objectMapper.writeValueAsString(tarefaService.listarTodasTarefas());
        });

        // Endpoint: Atualizar uma tarefa
        put("/api/tarefas/:id", (request, response) -> {
            response.type("application/json");
            try {
                int id = Integer.parseInt(request.params(":id"));
                Tarefa tarefa = objectMapper.readValue(request.body(), Tarefa.class);
                tarefa.setId(id);

                boolean atualizado = tarefaService.atualizarTarefa(tarefa);
                if (atualizado) {
                    response.status(200);
                    return objectMapper.writeValueAsString(tarefaService.buscarTarefaPorId(id));
                } else {
                    response.status(404);
                    return "{\"error\":\"Não foi possível atualizar. Tarefa não encontrada.\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"error\":\"ID de tarefa inválido.\"}";
            }
        });

        // Endpoint: Deletar uma tarefa
        delete("/api/tarefas/:id", (request, response) -> {
            response.type("application/json");
            try {
                int id = Integer.parseInt(request.params(":id"));
                boolean deletado = tarefaService.deletarTarefa(id);

                if (deletado) {
                    response.status(200);
                    return "{\"message\":\"Tarefa deletada com sucesso.\"}";
                } else {
                    response.status(404);
                    return "{\"error\":\"Não foi possível deletar. Tarefa não encontrada.\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"error\":\"ID de tarefa inválido.\"}";
            }
        });
    }
}