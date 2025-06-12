function showNavbar() {
    $('nav').toggleClass('responsive_nav');
}

// Exemplo: buscar usuários do backend
fetch('http://localhost:8080/api/usuarios')
  .then(response => response.json())
  .then(data => {
    console.log('Usuários:', data);
    // Aqui você pode atualizar o HTML com os dados recebidos
  })
  .catch(error => {
    console.error('Erro ao buscar usuários:', error);
  });