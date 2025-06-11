package com.inkids.controller;

import com.inkids.model.Usuario;
import com.inkids.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static spark.Spark.*;

/**
 * Controller para expor as operações de CRUD da entidade Usuario via API REST.
 */
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ObjectMapper objectMapper;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        this.objectMapper = new ObjectMapper();
        // Módulo para permitir que o ObjectMapper serialize/deserialize datas do Java 8+
        this.objectMapper.registerModule(new JavaTimeModule());

        setupRoutes();
    }

    private void setupRoutes() {

        // Endpoint: Criar um novo usuário
        post("/api/usuarios", (request, response) -> {
            response.type("application/json");
            try {
                Usuario usuario = objectMapper.readValue(request.body(), Usuario.class);
                Usuario novoUsuario = usuarioService.criarUsuario(usuario);

                if (novoUsuario != null) {
                    response.status(201); // 201 Created
                    return objectMapper.writeValueAsString(novoUsuario);
                } else {
                    response.status(400); // 400 Bad Request
                    return "{\"error\":\"Não foi possível criar o usuário. Verifique os dados enviados.\"}";
                }
            } catch (Exception e) {
                response.status(500);
                return "{\"error\":\"Ocorreu um erro no servidor: " + e.getMessage() + "\"}";
            }
        });

        // Endpoint: Buscar um usuário por ID
        get("/api/usuarios/:id", (request, response) -> {
            response.type("application/json");
            try {
                int id = Integer.parseInt(request.params(":id"));
                Usuario usuario = usuarioService.buscarUsuarioPorId(id);

                if (usuario != null) {
                    response.status(200); // 200 OK
                    return objectMapper.writeValueAsString(usuario);
                } else {
                    response.status(404); // 404 Not Found
                    return "{\"error\":\"Usuário não encontrado.\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"error\":\"ID de usuário inválido.\"}";
            }
        });

        // Endpoint: Listar todos os usuários
        get("/api/usuarios", (request, response) -> {
            response.type("application/json");
            response.status(200);
            return objectMapper.writeValueAsString(usuarioService.listarTodosUsuarios());
        });

        // Endpoint: Atualizar um usuário
        put("/api/usuarios/:id", (request, response) -> {
            response.type("application/json");
            try {
                int id = Integer.parseInt(request.params(":id"));
                Usuario usuario = objectMapper.readValue(request.body(), Usuario.class);
                usuario.setId(id); // Garante que o ID da URL seja usado

                boolean atualizado = usuarioService.atualizarUsuario(usuario);
                if (atualizado) {
                    response.status(200);
                    return objectMapper.writeValueAsString(usuarioService.buscarUsuarioPorId(id));
                } else {
                    response.status(404);
                    return "{\"error\":\"Não foi possível atualizar. Usuário não encontrado.\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"error\":\"ID de usuário inválido.\"}";
            }
        });

        // Endpoint: Deletar um usuário
        delete("/api/usuarios/:id", (request, response) -> {
            response.type("application/json");
            try {
                int id = Integer.parseInt(request.params(":id"));
                boolean deletado = usuarioService.deletarUsuario(id);

                if (deletado) {
                    response.status(200);
                    return "{\"message\":\"Usuário deletado com sucesso.\"}";
                } else {
                    response.status(404);
                    return "{\"error\":\"Não foi possível deletar. Usuário não encontrado.\"}";
                }
            } catch (NumberFormatException e) {
                response.status(400);
                return "{\"error\":\"ID de usuário inválido.\"}";
            }
        });
    }
}