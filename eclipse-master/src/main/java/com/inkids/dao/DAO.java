package com.inkids.dao;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Classe base para Acesso a Dados (Data Access Object).
 * Gerencia a conexão com o banco de dados e a inicialização do esquema.
 */
public abstract class DAO {

    // --- Detalhes da Conexão com o Banco de Dados ---
    // Usando H2 em memória: volátil, ótimo para desenvolvimento e testes.
    // Os dados são perdidos quando a aplicação para.
    private static final String JDBC_URL = "jdbc:h2:mem:inkidsdb;DB_CLOSE_DELAY=-1";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";

    protected Connection connection;

    public DAO() {
        this.connection = null;
    }

    /**
     * Abre uma conexão com o banco de dados.
     * @return true se a conexão for bem-sucedida, false caso contrário.
     */
    public boolean conectar() {
        try {
            // Garante que a conexão não seja recriada se já estiver aberta
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fecha a conexão com o banco de dados.
     * @return true se a conexão for fechada com sucesso ou se já estava nula/fechada, false se ocorrer um erro.
     */
    public boolean close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inicializa o banco de dados executando o script SQL de `src/main/resources`.
     * Este método deve ser chamado uma única vez no início da aplicação.
     * Em um ambiente de produção, ferramentas como Flyway ou Liquibase são recomendadas.
     */
    public static void initializeDatabase() {
        // Carrega o arquivo script.sql da pasta resources
        try (InputStream is = DAO.class.getResourceAsStream("/script-bd/script.sql")) {
            if (is == null) {
                System.err.println("Erro: script.sql não encontrado nos resources.");
                return;
            }

            // Lê o conteúdo do script
            String sqlScript;
              try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
               sqlScript = scanner.hasNext() ? scanner.next() : "";
         }
            
            // Conecta ao banco e executa o script
            try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
                 Statement stmt = conn.createStatement()) {
                
                // Executa o script inteiro
                stmt.execute(sqlScript);
                System.out.println("Banco de dados inicializado com sucesso.");

            } catch (SQLException e) {
                System.err.println("Erro ao inicializar o banco de dados: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Erro ao ler o arquivo script.sql: " + e.getMessage());
            e.printStackTrace();
        }
    }
}