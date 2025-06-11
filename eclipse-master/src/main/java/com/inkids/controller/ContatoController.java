package com.inkids.controller;

import com.inkids.model.Contato;
import com.inkids.service.ContatoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static spark.Spark.*;

/**
 * Controller para expor as operações da entidade Contato via API REST.
 */
public class ContatoController {

    private final ContatoService contatoService;
    private final ObjectMapper objectMapper;

    public ContatoController(ContatoService contatoService) {
        this.contatoService = contatoService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        setupRoutes();
    }

    private void setupRoutes() {

        // Endpoint: Receber uma nova mensagem de contato
        post("/api/contatos", (request, response) -> {
            response.type("application/json");
            try {
                Contato contato = objectMapper.readValue(request.body(), Contato.class);
                Contato novaMensagem = contatoService.salvarMensagem(contato);

                if (novaMensagem != null) {
                    response.status(201); // 201 Created
                    return "{\"message\":\"Mensagem recebida com sucesso!\"}";
                } else {
                    response.status(400); // 400 Bad Request
                    return "{\"error\":\"Não foi possível enviar a mensagem. Verifique os dados.\"}";
                }
            } catch (Exception e) {
                response.status(500);
                return "{\"error\":\"Ocorreu um erro no servidor: " + e.getMessage() + "\"}";
            }
        });

        // Endpoint: Listar todas as mensagens de contato (para admin)
        get("/api/contatos", (request, response) -> {
            response.type("application/json");
            response.status(200);
            return objectMapper.writeValueAsString(contatoService.listarTodasMensagens());
        });

        // Endpoint: Deletar uma mensagem de contato (para admin)
        delete("/api/contatos/:id", (request, response) -> {
            response.type("application/json");
            try {
                int id = Integer.parseInt(request.params(":id"));
                boolean deletado = contatoService.deletarMensagem(id);

                if (deletado) {
                    response.status(200);
                    return "{\"message\":\"Mensagem deletada com sucesso.\"}";
                } else {
                    response.status(404);
                    return "{\"error\":\"Não foi possível deletar. Mensagem não encontrada.\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"error\":\"ID de contato inválido.\"}";
            }
        });
    }
}