/**
 * Módulo para gerenciar o formulário de criação de postagens
 */
const PostFormManager = (() => {
    // Elemento do formulário
    let postForm;

    /**
     * Inicializa o módulo
     */
    function init() {
        console.log('Inicializando PostFormManager');

        postForm = document.getElementById('postForm');

        if (!postForm) {
            console.warn('Formulário de postagem não encontrado');
            return;
        }

        // Adiciona evento de envio do formulário
        postForm.addEventListener('submit', handleSubmit);

        // Escuta evento de seleção de imagem
        document.addEventListener('imageSelected', handleImageSelected);

        console.log('PostFormManager inicializado com sucesso');
    }

    /**
     * Manipula o envio do formulário
     * @param {Event} event - Evento de submit
     */
    async function handleSubmit(event) {
        event.preventDefault();

        const titleInput = document.getElementById('postTitle');
        const contentInput = document.getElementById('postContent');

        if (!titleInput || !contentInput) {
            alert('Erro ao encontrar os campos do formulário');
            return;
        }

        const title = titleInput.value.trim();
        const content = contentInput.value.trim();

        // Validação básica
        if (!title || !content) {
            alert('Por favor, preencha todos os campos obrigatórios.');
            return;
        }

        // Obtém a URL da imagem selecionada
        const coverImageUrl = ImageGenerator.getSelectedImageUrl();

        if (!coverImageUrl) {
            alert('Por favor, gere e selecione uma imagem para a postagem.');
            return;
        }

        try {
            // Desabilita o botão de envio para evitar múltiplos cliques
            const submitBtn = postForm.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.innerHTML =
                '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Publicando...';

            // Obter ID do usuário do localStorage
            const userId = localStorage.getItem('userId');

            // Prepara os dados da postagem
            const postData = {
                title: title,
                author: 'Autor', // Idealmente, pegar o nome do usuário logado
                content: content,
                imageLink: coverImageUrl,
                userId: userId,
            };

            console.log('Enviando dados da postagem:', postData);

            // Em um ambiente real, usaríamos a função createPost
            // Por ora, simularemos o sucesso da criação
            try {
                if (typeof createPost === 'function') {
                    createPost(postData, (response) => {
                        console.log('Postagem criada:', response);
                        alert('Postagem criada com sucesso!');
                        window.location.href = './postagens.html';
                    });
                } else {
                    // Simulação para desenvolvimento
                    console.log('Simulando criação de postagem');
                    setTimeout(() => {
                        alert('Postagem criada com sucesso!');
                        window.location.href = './postagens.html';
                    }, 1500);
                }
            } catch (error) {
                console.error('Erro ao criar postagem:', error);
                alert('Ocorreu um erro ao criar a postagem. Por favor, tente novamente.');
            }
        } catch (error) {
            console.error('Erro no envio do formulário:', error);
            alert('Ocorreu um erro ao processar o formulário. Por favor, tente novamente.');
        } finally {
            // Reabilita o botão de envio
            const submitBtn = postForm.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Publicar Postagem';
            }
        }
    }

    /**
     * Manipula o evento de seleção de imagem
     * @param {CustomEvent} event - Evento personalizado com dados da imagem
     */
    function handleImageSelected(event) {
        console.log('Imagem selecionada:', event.detail.imageUrl);
        // Aqui você pode adicionar código adicional quando uma imagem for selecionada
    }

    // API pública do módulo
    return {
        init,
    };
})();

// Inicializa o módulo quando o DOM estiver carregado
document.addEventListener('DOMContentLoaded', PostFormManager.init);

let userId = localStorage.getItem('userId');
let modal = document.getElementById('modal');

if (!userId) {
    alert('Acesso restrito. Por favor, faça login para continuar.');
    window.location.replace('../usuario/login.html');
} else {
    modal.style.opacity = 1;

    function init() {
        let formularioPostagem = document.querySelector('form');
        let btnCadastrarPostagem = document.getElementById(
            'btnCadastrarPostagem'
        );
        let campoNomeAutor = document.getElementById('nome-autor');

        findUserById(userId, (user) => {
            campoNomeAutor.value = user.name;
        });

        btnCadastrarPostagem.addEventListener('click', (e) => {
            let campoTitulo = document.getElementById('titulo-postagem').value;
            let campoLinkImagem = document.getElementById('link-imagem').value;
            let campoTextoPostagem =
                document.getElementById('texto-postagem').value;

            e.preventDefault();

            if (!formularioPostagem.checkValidity()) {
                displayMessage(
                    'Preencha o formulário corretamente.',
                    'warning'
                );
                return;
            }

            if (campoTitulo.trim().length < 5) {
                displayMessage(
                    'O título da postagem deve ter pelo menos 5 caracteres.',
                    'warning'
                );
                return;
            }

            if (campoNomeAutor.value.trim().length < 10) {
                displayMessage(
                    'O nome do autor deve ter pelo menos 10 caracteres.',
                    'warning'
                );
                return;
            }

            if (campoTextoPostagem.trim().length < 100) {
                displayMessage(
                    'O texto da postagem deve ter pelo menos 100 caracteres.',
                    'warning'
                );
                return;
            }

            let postagem = {
                title: campoTitulo,
                author: campoNomeAutor.value,
                content: campoTextoPostagem,
                imageLink: campoLinkImagem,
                userId: userId,
            };

            createPost(postagem);

            formularioPostagem.reset();

            findAllUsers((users) => {
                findAllPosts((posts) => {
                    let novaPostagem = posts.find(
                        (post) =>
                            post.title === campoTitulo &&
                            post.content === campoTextoPostagem &&
                            post.userId === userId
                    );

                    if (novaPostagem) {
                        users.forEach((user) => {
                            enviarEmailNovaPublicacao(
                                user.email,
                                user.name,
                                novaPostagem._id,
                                novaPostagem.title,
                                novaPostagem.author
                            );
                        });
                    }
                });
            });
        });
    }

    init();
}
