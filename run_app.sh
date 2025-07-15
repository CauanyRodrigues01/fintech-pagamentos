#!/bin/bash

# --- Script para construir e iniciar a aplicação Spring Boot e o PostgreSQL com Docker Compose ---

echo "1. Navegando para a pasta backend..."
cd backend || { echo "Erro: Pasta 'backend' não encontrada. Certifique-se de estar na raiz do repositório."; exit 1; }

echo "2. Construindo o projeto Spring Boot com Maven (gerando o JAR)..."
# O -DskipTests é usado para pular os testes que podem demorar ou exigir Docker Desktop
./mvnw clean install -DskipTests
if [ $? -ne 0 ]; then
    echo "Erro: A construção do projeto Maven falhou. Verifique os logs acima."
    exit 1
fi

echo "3. Voltando para a raiz do repositório..."
cd ..

echo "4. Parando e removendo contêineres e volumes antigos (para garantir um ambiente limpo)..."
# O -v remove volumes de dados, o que é crucial para reiniciar o DB do zero,
# mas pode ser removido se você quiser manter os dados do DB entre as execuções.
docker-compose down -v
# docker-compose down # Use este se quiser manter os dados do DB_data entre as execuções

echo "5. Construindo as imagens Docker e iniciando os serviços..."
# --build força a reconstrução das imagens (backend, db se a imagem mudar)
# -d roda os contêineres em segundo plano
docker-compose up --build -d
if [ $? -ne 0 ]; then
    echo "Erro: O Docker Compose falhou ao iniciar os serviços. Verifique os logs acima."
    exit 1
fi

echo "--- Aplicação Iniciada com Sucesso! ---"
echo "Backend (API): http://localhost:8080"
echo "Swagger UI (Documentação da API): http://localhost:8080/swagger-ui.html"
echo "Frontend (Web Interface): Abra o arquivo 'frontend/index.html' no seu navegador."
echo "-------------------------------------"