/**
 * Módulo para geração de imagens usando serviços de IA
 */
const ImageGenerator = (() => {
    // Elementos do DOM
    let generateBtn;
    let loadingElement;
    let imagesContainer;
    let errorContainer;
    let errorMessage;
    let imageOptions;
    
    // Armazena a URL da imagem selecionada
    let selectedImageUrl = null;
    
    // Fallback local de imagens caso a API falhe completamente
    const fallbackImages = [
        '../../assets/images/fallback/image1.jpg',
        '../../assets/images/fallback/image2.jpg',
        '../../assets/images/fallback/image3.jpg',
        '../../assets/images/fallback/image4.jpg'
    ];
    
    /**
     * Inicializa os eventos
     */
    function init() {
        console.log('Inicializando ImageGenerator');
        
        // Inicializar referências aos elementos DOM
        generateBtn = document.getElementById('generateImagesBtn');
        loadingElement = document.getElementById('loadingImages') || createLoadingElement();
        imagesContainer = document.getElementById('generatedImagesContainer');
        errorContainer = document.getElementById('errorContainer') || createErrorElement();
        errorMessage = errorContainer ? errorContainer.querySelector('#errorMessage') : null;
        imageOptions = document.querySelectorAll('.image-option');
        
        if (!generateBtn) {
            console.warn('Botão de gerar imagens não encontrado');
            return;
        }
        
        // Evento para gerar as imagens
        generateBtn.addEventListener('click', handleGenerateImages);
        
        // Evento para selecionar imagem
        if (imageOptions && imageOptions.length > 0) {
            imageOptions.forEach(option => {
                option.addEventListener('click', function() {
                    selectImage(this);
                });
            });
        }
        
        console.log('ImageGenerator inicializado com sucesso');
    }
    
    /**
     * Cria elemento de carregamento se não existir no DOM
     */
    function createLoadingElement() {
        const element = document.createElement('div');
        element.id = 'loadingImages';
        element.className = 'loading-container d-none text-center my-3';
        element.innerHTML = `
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Gerando imagens...</span>
            </div>
            <p class="mt-2">Gerando imagens, aguarde um momento...</p>
        `;
        
        // Adicionar após o botão de gerar imagens
        if (generateBtn && generateBtn.parentNode) {
            generateBtn.parentNode.appendChild(element);
        }
        
        return element;
    }
    
    /**
     * Cria elemento de erro se não existir no DOM
     */
    function createErrorElement() {
        const element = document.createElement('div');
        element.id = 'errorContainer';
        element.className = 'alert alert-danger d-none my-3';
        element.innerHTML = `
            <i class="fas fa-exclamation-triangle me-2"></i>
            <span id="errorMessage">Ocorreu um erro ao gerar as imagens.</span>
        `;
        
        // Adicionar após o botão de gerar imagens ou loading
        const insertAfter = loadingElement || generateBtn;
        if (insertAfter && insertAfter.parentNode) {
            insertAfter.parentNode.appendChild(element);
        }
        
        return element;
    }
    
    /**
     * Manipula o clique no botão de gerar imagens
     */
    async function handleGenerateImages() {
        console.log('Botão de gerar imagens clicado');
        
        const titleInput = document.getElementById('postTitle');
        
        if (!titleInput) {
            console.error('Campo de título não encontrado');
            showError('Erro na interface: campo de título não encontrado');
            return;
        }
        
        const title = titleInput.value.trim();
        
        if (!title) {
            showError('Por favor, insira um título para a postagem antes de gerar imagens.');
            titleInput.focus();
            return;
        }

        // Garante que o container de imagens e o de erro estejam ocultos inicialmente ao tentar nova geração
        if (imagesContainer) {
            imagesContainer.classList.add('d-none');
        }
        hideError();
        showLoading(true); // Mostra o loader
        
        try {
            // Esconde mensagens de erro anteriores - movido para cima
            // hideError(); 
            
            // Mostra loader - movido para cima
            // showLoading(true);
            
            console.log('Iniciando geração de imagens para título:', title);
            const imageUrls = await generateImagesFromTitle(title);
            
            if (!imageUrls || imageUrls.length === 0) {
                throw new Error('Nenhuma imagem foi gerada');
            }
            
            console.log('Imagens geradas:', imageUrls);
            displayGeneratedImages(imageUrls);
            showLoading(false); // Esconde o loader após sucesso
            
        } catch (error) {
            console.error('Erro ao gerar imagens:', error);
            showError('Não foi possível gerar as imagens. Por favor, tente novamente mais tarde.');
            showLoading(false); // Esconde o loader em caso de erro
            
            // Se houver imagens na página, use-as como fallback
            const existingImages = document.querySelectorAll('.image-option img');
            if (existingImages && existingImages.length > 0) {
                const hasValidSrc = Array.from(existingImages).some(img => 
                    img.src && img.src !== 'about:blank' && !img.src.endsWith('/undefined')
                );
                
                if (!hasValidSrc) {
                    console.log('Usando imagens de fallback locais');
                    displayGeneratedImages(fallbackImages);
                }
            }
        }
    }
    
    /**
     * Gera imagens a partir do título usando o serviço de API
     */
    async function generateImagesFromTitle(title) {
        try {
            const response = await fetch('http://localhost:8080/api/generate-images', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    title: title,
                    count: 4
                })
            });
            
            if (!response.ok) {
                const errorText = await response.text();
                console.error('Erro na resposta:', response.status, errorText);
                throw new Error(`Falha na comunicação com o servidor (${response.status})`);
            }
            
            const data = await response.json();
            
            if (!data.success) {
                throw new Error(data.message || 'Falha ao gerar imagens');
            }
            
            return data.images;
            
        } catch (error) {
            console.error('Erro na geração de imagens:', error);
            throw error;
        }
    }
    
    /**
     * Exibe as imagens geradas na interface
     */
    function displayGeneratedImages(imageUrls) {
        const imageContainers = document.querySelectorAll('.image-option');
        
        if (!imageContainers || imageContainers.length === 0) {
            console.error('Containers de imagem não encontrados');
            // Se os containers não existem, não há onde mostrar, então esconda o loading e mostre um erro.
            showLoading(false);
            showError('Erro na interface: containers de imagem não encontrados.');
            return;
        }
        
        // Atualiza as imagens com as URLs geradas
        imageContainers.forEach((container, index) => {
            const img = container.querySelector('img') || document.createElement('img');
            
            if (!img.parentNode) {
                img.className = 'img-fluid';
                container.appendChild(img);
            }
            
            if (imageUrls[index]) {
                // Suporta objetos ({url}) ou strings de URL
                const src = typeof imageUrls[index] === 'string' ? imageUrls[index] : imageUrls[index].url;
                img.src = src;
                
                // Tratamento de erro de carregamento
                img.onerror = function() {
                    console.warn(`Erro ao carregar imagem ${index + 1}, usando placeholder`);
                    this.onerror = null; // Evita loop infinito
                    this.src = `https://via.placeholder.com/800x600?text=Imagem+${index + 1}`;
                };
            }
        });
        
        clearImageSelection();
        
        // Certifica-se que o container de imagens geradas esteja visível
        if (imagesContainer) {
            imagesContainer.classList.remove('d-none');
        }

        // Esconde o container de erro se tudo correu bem até aqui
        // hideError(); // Removido pois o erro deve ser tratado no catch de handleGenerateImages
    }
    
    /**
     * Mostra ou esconde o indicador de carregamento
     */
    function showLoading(show) {
        if (loadingElement) {
            if (show) {
                loadingElement.classList.remove('d-none');
            } else {
                loadingElement.classList.add('d-none');
            }
        }
    }
    
    /**
     * Exibe mensagem de erro
     */
    function showError(message) {
        if (errorMessage && errorContainer) {
            errorMessage.textContent = message;
            errorContainer.classList.remove('d-none');
        } else {
            alert('Erro: ' + message);
        }
    }
    
    /**
     * Esconde mensagem de erro
     */
    function hideError() {
        if (errorContainer) {
            errorContainer.classList.add('d-none');
        }
    }
    
    /**
     * Seleciona uma imagem como capa
     */
    function selectImage(element) {
        // Remove seleção anterior
        clearImageSelection();
        
        // Adiciona classe de seleção
        element.classList.add('selected');
        
        // Armazena URL da imagem selecionada
        const img = element.querySelector('img');
        if (img && img.src) {
            selectedImageUrl = img.src;
            
            // Emite evento de seleção para outros módulos
            document.dispatchEvent(new CustomEvent('imageSelected', {
                detail: { imageUrl: selectedImageUrl }
            }));
            
            console.log('Imagem selecionada:', selectedImageUrl);
        }
    }
    
    /**
     * Limpa a seleção atual de imagem
     */
    function clearImageSelection() {
        const options = document.querySelectorAll('.image-option');
        options.forEach(option => {
            option.classList.remove('selected');
        });
        selectedImageUrl = null;
    }
    
    /**
     * Retorna a URL da imagem selecionada
     */
    function getSelectedImageUrl() {
        return selectedImageUrl;
    }
    
    // API pública do módulo
    return {
        init,
        getSelectedImageUrl
    };
})();

// Inicializa o módulo quando o DOM estiver carregado
document.addEventListener('DOMContentLoaded', ImageGenerator.init);