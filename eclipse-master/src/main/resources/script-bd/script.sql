-- Remove as tabelas existentes para garantir uma inicialização limpa (útil em desenvolvimento)
-- A ordem é importante para evitar erros de chave estrangeira
DROP TABLE IF EXISTS Contatos;
DROP TABLE IF EXISTS Tarefas;
DROP TABLE IF EXISTS Postagens;
DROP TABLE IF EXISTS Usuarios;


-- Criação da tabela de Usuários
-- Armazena as informações dos usuários que podem se logar no sistema.
CREATE TABLE IF NOT EXISTS Usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- Identificador único para cada usuário
    nome VARCHAR(255) NOT NULL,                 -- Nome completo do usuário
    email VARCHAR(255) UNIQUE NOT NULL,         -- Email para login, deve ser único
    senha VARCHAR(255) NOT NULL,                -- Senha (em produção, deve ser um hash)
    data_nascimento DATE,                       -- Data de nascimento do usuário
    genero VARCHAR(50),                         -- Gênero do usuário
    telefone VARCHAR(20),                       -- Telefone de contato
    tipo_usuario VARCHAR(50) DEFAULT 'USER',    -- Tipo de usuário (ex: 'USER', 'ADMIN')
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data e hora de criação do registro
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Data e hora da última atualização
);

-- Criação da tabela de Postagens
-- Armazena os artigos ou postagens do blog.
CREATE TABLE IF NOT EXISTS Postagens (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- Identificador único para cada postagem
    titulo VARCHAR(255) NOT NULL,               -- Título da postagem, usado para gerar a imagem
    conteudo TEXT NOT NULL,                     -- Corpo da postagem
    autor_id INTEGER NOT NULL,                  -- Chave estrangeira para o autor (da tabela Usuarios)
    imagem_url VARCHAR(1024),                   -- URL da imagem gerada pela IA
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data e hora de criação
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data e hora da última atualização
    FOREIGN KEY (autor_id) REFERENCES Usuarios(id) ON DELETE CASCADE -- Se o usuário for deletado, suas postagens também são
);

-- Criação da tabela de Tarefas
-- Armazena uma lista de tarefas para os usuários.
CREATE TABLE IF NOT EXISTS Tarefas (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- Identificador único para cada tarefa
    titulo VARCHAR(255) NOT NULL,               -- Título da tarefa
    descricao TEXT,                             -- Descrição detalhada da tarefa
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data de criação da tarefa
    data_conclusao TIMESTAMP,                   -- Data em que a tarefa foi concluída
    status VARCHAR(50) DEFAULT 'PENDENTE',      -- Status atual (PENDENTE, EM_ANDAMENTO, CONCLUIDA)
    prioridade VARCHAR(50) DEFAULT 'MEDIA',     -- Prioridade da tarefa (BAIXA, MEDIA, ALTA)
    usuario_id INTEGER NOT NULL,                -- Chave estrangeira para o usuário dono da tarefa
    FOREIGN KEY (usuario_id) REFERENCES Usuarios(id) ON DELETE CASCADE -- Se o usuário for deletado, suas tarefas também são
);

-- Criação da tabela de Contatos
-- Armazena as mensagens enviadas pelo formulário de contato do site.
CREATE TABLE IF NOT EXISTS Contatos (
    id INT AUTO_INCREMENT PRIMARY KEY,          -- Identificador único para cada mensagem
    nome VARCHAR(255) NOT NULL,                 -- Nome da pessoa que enviou a mensagem
    email VARCHAR(255) NOT NULL,                -- Email de contato
    telefone VARCHAR(20),                       -- Telefone de contato (opcional)
    assunto VARCHAR(255),                       -- Assunto da mensagem
    mensagem TEXT NOT NULL,                     -- A mensagem em si
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Data de recebimento da mensagem
);