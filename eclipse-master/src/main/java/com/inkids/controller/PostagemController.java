package com.inkids.controller;

import com.inkids.model.Postagem;
import com.inkids.service.PostagemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static spark.Spark.*;

/**
 * Controller para expor as operações de CRUD da entidade Postagem via API REST.
 */
public class PostagemController {

    private final PostagemService postagemService;
    private final ObjectMapper objectMapper;

    public PostagemController(PostagemService postagemService) {
        this.postagemService = postagemService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        setupRoutes();
    }

    private void setupRoutes() {

        // Endpoint: Criar uma nova postagem (com geração de imagem por IA)
        post("/api/postagens", (request, response) -> {
            response.type("application/json");
            try {
                Postagem postagem = objectMapper.readValue(request.body(), Postagem.class);
                Postagem novaPostagem = postagemService.criarPostagem(postagem);

                if (novaPostagem != null) {
                    response.status(201); // 201 Created
                    return objectMapper.writeValueAsString(novaPostagem);
                } else {
                    response.status(400); // 400 Bad Request
                    return "{\"error\":\"Não foi possível criar a postagem. Verifique os dados enviados.\"}";
                }
            } catch (Exception e) {
                response.status(500);
                return "{\"error\":\"Ocorreu um erro no servidor: " + e.getMessage() + "\"}";
            }
        });

        // Endpoint: Buscar uma postagem por ID
        get("/api/postagens/:id", (request, response) -> {
            response.type("application/json");
            try {
                int id = Integer.parseInt(request.params(":id"));
                Postagem postagem = postagemService.buscarPostagemPorId(id);

                if (postagem != null) {
                    response.status(200); // 200 OK
                    return objectMapper.writeValueAsString(postagem);
                } else {
                    response.status(404); // 404 Not Found
                    return "{\"error\":\"Postagem não encontrada.\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"error\":\"ID de postagem inválido.\"}";
            }
        });

        // Endpoint: Listar todas as postagens ou as de um usuário específico
        get("/api/postagens", (request, response) -> {
            response.type("application/json");
            String usuarioIdParam = request.queryParams("usuarioId");

            if (usuarioIdParam != null && !usuarioIdParam.isEmpty()) {
                // Lista postagens por usuário
                try {
                    int usuarioId = Integer.parseInt(usuarioIdParam);
                    return objectMapper.writeValueAsString(postagemService.listarPostagensPorUsuario(usuarioId));
                } catch (NumberFormatException e) {
                    response.status(400);
                    return "{\"error\":\"ID de usuário inválido.\"}";
                }
            } else {
                // Lista todas as postagens
                return objectMapper.writeValueAsString(postagemService.listarTodasPostagens());
            }
        });

        // Endpoint: Atualizar uma postagem
        put("/api/postagens/:id", (request, response) -> {
            response.type("application/json");
            try {
                int id = Integer.parseInt(request.params(":id"));
                Postagem postagem = objectMapper.readValue(request.body(), Postagem.class);
                postagem.setId(id);

                boolean atualizado = postagemService.atualizarPostagem(postagem);
                if (atualizado) {
                    response.status(200);
                    return objectMapper.writeValueAsString(postagemService.buscarPostagemPorId(id));
                } else {
                    response.status(404);
                    return "{\"error\":\"Não foi possível atualizar. Postagem não encontrada.\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"error\":\"ID de postagem inválido.\"}";
            }
        });

        // Endpoint: Deletar uma postagem
        delete("/api/postagens/:id", (request, response) -> {
            response.type("application/json");
            try {
                int id = Integer.parseInt(request.params(":id"));
                boolean deletado = postagemService.deletarPostagem(id);

                if (deletado) {
                    response.status(200);
                    return "{\"message\":\"Postagem deletada com sucesso.\"}";
                } else {
                    response.status(404);
                    return "{\"error\":\"Não foi possível deletar. Postagem não encontrada.\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"error\":\"ID de postagem inválido.\"}";
            }
        });
    }
}