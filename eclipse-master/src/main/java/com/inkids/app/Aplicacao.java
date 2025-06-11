package com.inkids.app;
// const postRouteUrl = 'http://localhost:8080/api/postagens';

import com.inkids.controller.*;
import com.inkids.dao.DAO;
import com.inkids.model.Usuario;
import com.inkids.service.*;

import java.time.LocalDate;

import static spark.Spark.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Classe principal que inicia a aplicação backend.
 */
public class Aplicacao {

    public static void main(String[] args) {
        // 1. Inicializa o esquema do banco de dados na memória.
        // Deve ser a primeira ação para garantir que as tabelas existam.
        DAO.initializeDatabase();

        // 2. Configura a porta do servidor web.
        // A porta 8080 é uma alternativa comum à padrão 4567.
        port(8080);

        // 3. Configura o CORS (Cross-Origin Resource Sharing).
        // Essencial para permitir que o front-end faça requisições para este servidor.
        enableCORS();

        // 4. Instancia todas as camadas de serviço.
        UsuarioService usuarioService = new UsuarioService();
        PostagemService postagemService = new PostagemService();
        TarefaService tarefaService = new TarefaService();
        ContatoService contatoService = new ContatoService();

        // 5. Instancia todos os controllers, passando os serviços correspondentes.
        // Isso registra todas as rotas da API (/api/usuarios, /api/postagens, etc.).
        new UsuarioController(usuarioService);
        new PostagemController(postagemService);
        new TarefaController(tarefaService);
        new ContatoController(contatoService);

        // Nova rota para geração de imagens
        post("/api/generate-images", (req, res) -> {
            res.type("application/json");
            String body = req.body();
            String prompt = "imagem"; // valor padrão

            try {
                // Extrai o título do JSON recebido
                 JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                 if (json.has("title")) {
                  prompt = json.get("title").getAsString();
                 }
            } catch (Exception e) {
                // Se der erro, usa o valor padrão
            }

            GeminiImageService imageService = new GeminiImageService();
            String imageUrl = imageService.generateImageUrl(prompt);

            // Retorna um JSON com a imagem (em array, pois o front espera várias)
            return "{\"success\":true,\"images\":[\"" + imageUrl + "\"]}";
        });

        System.out.println("\nServidor Java (Spark) iniciado com sucesso!");
        System.out.println("Ouvindo na porta: http://localhost:8080");
        System.out.println("Endpoints da API estão disponíveis em /api/*");

        // 6. Executa a demonstração das operações de CRUD (opcional).
        // Descomente a linha abaixo para ver as operações no console ao iniciar.
        // demonstrateCrudOperations(usuarioService, postagemService, tarefaService, contatoService);
    }

    /**
     * Habilita o CORS para permitir requisições de origens diferentes.
     * Permite que um front-end rodando em localhost:3000, por exemplo, acesse a API.
     */
    private static void enableCORS() {
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*"); // Permite qualquer origem
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
            response.type("application/json");
        });
    }

    /**
     * Método para demonstrar e testar as operações de CRUD programaticamente,
     * conforme solicitado no prompt original.
     */
    private static void demonstrateCrudOperations(UsuarioService usuarioService, PostagemService postagemService,TarefaService tarefaService, ContatoService contatoService) {
        System.out.println("\n--- INÍCIO DA DEMONSTRAÇÃO DE OPERAÇÕES CRUD ---");

        // --- CRUD Usuário ---
        System.out.println("\n** CRUD Usuário **");
        Usuario novoUsuario = new Usuario("Davi Lanna", "davi.lanna@example.com", "senhaSegura123", LocalDate.now().minusYears(25), "Masculino", "31999998888", "ADMIN");
        novoUsuario = usuarioService.criarUsuario(novoUsuario);
        if (novoUsuario != null) {
            System.out.println("1. INSERT: Usuário inserido com ID: " + novoUsuario.getId());
            
            Usuario usuarioBuscado = usuarioService.buscarUsuarioPorId(novoUsuario.getId());
            System.out.println("2. GET: Usuário buscado: " + usuarioBuscado.getNome());
            
            System.out.println("3. LISTAR: Total de usuários: " + usuarioService.listarTodosUsuarios().size());
            
            usuarioBuscado.setNome("Davi Lanna (Atualizado)");
            boolean atualizado = usuarioService.atualizarUsuario(usuarioBuscado);
            System.out.println("4. UPDATE: Usuário atualizado? " + atualizado);
            
            boolean removido = usuarioService.deletarUsuario(usuarioBuscado.getId());
            System.out.println("5. REMOVE: Usuário removido? " + removido);
        } else {
            System.out.println("Falha ao criar usuário para demonstração.");
        }

        System.out.println("\n--- FIM DA DEMONSTRAÇÃO ---");
    }
}