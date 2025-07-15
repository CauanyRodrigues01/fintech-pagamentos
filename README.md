# 🚀 API de Pagamentos - Soluções Financeiras para Varejo

## Visão Geral do Projeto

Este projeto é uma implementação de um pequeno sistema de gestão de clientes, faturas e pagamentos, simulando parte do ecossistema de uma fintech. Desenvolvido para demonstrar habilidades em desenvolvimento fullstack, arquitetura em camadas, persistência de dados, validação, tratamento de exceções, e conteinerização com Docker.

## 🎯 Contexto do Desafio

Você foi contratado(a) como estagiário(a) na equipe de engenharia de software de uma fintech que oferece soluções de pagamento para o varejo. Seu primeiro desafio foi criar um sistema para gerenciar clientes, faturas e pagamentos, com uma API funcional e uma interface web simples, mimetizando um ambiente real da empresa.

## 🛠️ Tecnologias Utilizadas

### Backend

* **Java 21**
* **Spring Boot 3.5.3**
* **Spring Data JPA**
* **PostgreSQL 17**
* **Lombok:** Biblioteca para reduzir boilerplate code (getters, setters, construtores).
* **ModelMapper:** Ferramenta para mapeamento de objetos entre camadas (Entidade ↔ DTO).
* **SpringDoc OpenAPI (Swagger UI):** Para documentação automática e interativa da API.
* **Jakarta Validation (Bean Validation):** Para validação de dados em nível de aplicação.

### Frontend

* **HTML5:** Estrutura das páginas web.
* **CSS3:** Estilização e layout.
* **JavaScript:** Lógica interativa do lado do cliente (requisições à API, manipulação do DOM).

### Infraestrutura & Ferramentas

* **Docker & Docker Compose:** Conteinerização do ambiente de desenvolvimento (banco de dados e backend).
* **Maven:** Ferramenta de gerenciamento de build e dependências.
* **JUnit 5:** Framework para testes unitários.
* **Mockito:** Framework para criação de mocks em testes unitários.
* **Testcontainers:** Biblioteca para testes de integração com banco de dados real em contêineres Docker.

## 📁 Estrutura do Repositório

O projeto segue uma estrutura de monorepo simplificada:

```
seu-repositorio-raiz/
├── backend/                # Aplicação Spring Boot
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile          # Dockerfile para a aplicação backend
├── frontend/               # Aplicação frontend (HTML, CSS, JS)
│   ├── index.html          # Listagem e cadastro de clientes
│   ├── faturas.html        # Listagem de faturas de um cliente
│   └── assets/
├── database/               # Scripts SQL de inicialização do banco de dados
│   ├── 01_schema.sql       # Criação de tabelas
│   └── 02_seed.sql         # População de dados iniciais
├── .gitignore
└── docker-compose.yml      # Orquestração dos contêineres Docker
```

## ⚙️ Pré-requisitos

Antes de rodar a aplicação, certifique-se de ter instalado:

* **Java Development Kit (JDK) 21**
* **Apache Maven** (versão 3.x ou superior)
* **Docker Desktop** (ou daemon Docker para Linux/Mac) - **Deve estar rodando**

## 🚀 Como Rodar a Aplicação

Siga estes passos para configurar e iniciar o ambiente completo:

1. **Clone o Repositório:**
   
   ```bash
   git clone https://github.com/CauanyRodrigues01/fintech-pagamentos.git
   cd fintech-pagamentos # Navegue até a raiz do repositório
   ```

2. **Execute o Script de Inicialização:**
   
   - Navegue até a **raiz do seu repositório** (onde está o arquivo `run_app.sh`).
   
   - **Para Linux/macOS:** Dê permissão de execução ao script (apenas uma vez):
     
     Bash
     
     ```
     chmod +x run_app.sh
     ```
   
   - **Para Linux/macOS/Windows (usando Git Bash/WSL):** Execute o script:
     
     Bash
     
     ```
     ./run_app.sh
     ```
     
     - Este script irá:
       
       - Navegar para a pasta `backend/`.
       
       - Construir o projeto Spring Boot (gerando o JAR).
       
       - Remover contêineres e volumes Docker antigos (garantindo um ambiente limpo para o DB).
       
       - Construir as imagens Docker dos seus serviços.
       
       - Iniciar o contêiner do PostgreSQL e o contêiner da sua aplicação Spring Boot em segundo plano.
   
   - Aguarde a conclusão do script. Ele fornecerá mensagens de progresso e, ao final, as URLs para acesso à aplicação.

3. **Acesse a API (Backend):**
   
   * Após o contêiner `fintech_backend` estar `Up`, a API estará disponível em `http://localhost:8080`.
   * Você pode explorar a documentação interativa do Swagger UI em:
     **`http://localhost:8080/swagger-ui.html`**

4. **Acesse o Frontend:**
   
   * Abra o arquivo `frontend/index.html` diretamente no seu navegador.
   * Navegue entre as telas para interagir com a API.
   * **Observação:** O backend tem CORS configurado para `*` em desenvolvimento para permitir a comunicação com o frontend rodando via `file://` ou em outra porta.

## ✨ Funcionalidades Implementadas

### Banco de Dados

* **Modelagem Relacional:** Tabelas `Cliente` e `Fatura` com chaves primárias (UUIDs) e estrangeiras.
* **Inicialização Automática:** Scripts SQL (`01_schema.sql`, `02_seed.sql`) executados automaticamente na primeira inicialização do contêiner PostgreSQL para criar o esquema e popular dados.

### Backend (API REST)

* **Clientes:**
  * `GET /clientes`: Lista todos os clientes.
  * `POST /clientes`: Cadastra novo cliente (com validação de dados).
  * `GET /clientes/{id}`: Consulta cliente por ID.
  * `PUT /clientes/{id}`: Atualiza cliente (com validação) e implementa a regra de bloqueio/zeramento de crédito.
  * `GET /clientes/bloqueados`: Lista clientes com status 'Bloqueado'.
* **Faturas:**
  * `GET /faturas`: Lista todas as faturas.
  * `GET /faturas/{clienteId}`: Lista todas as faturas de um cliente específico.
  * `PUT /faturas/{id}/pagamento`: Registra pagamento para uma fatura (com validação).
  * `GET /faturas/atrasadas`: Lista faturas com status 'Atrasada'.
* **Regras de Negócio:**
  * Ao registrar pagamento, status da fatura muda para "Paga".
  * Clientes bloqueados têm limite de crédito atualizado para R$ 0,00.
  * **Job Agendado:** Um job diário (executa às 00:00:00, configurável) verifica faturas com mais de 3 dias de atraso (`status='A'`) e automaticamente bloqueia o cliente associado, zerando seu limite de crédito.
* **Tratamento Global de Exceções:** Implementado com `@ControllerAdvice` para fornecer respostas de erro padronizadas (HTTP Status Code e corpo JSON detalhado) para validações (`400 Bad Request`), recursos não encontrados (`404 Not Found`) e erros internos (`500 Internal Server Error`).
* **Mapeamento de DTOs:** Utilização de DTOs de Requisição e Resposta (`ClienteRequestDTO`, `ClienteResponseDTO`, `FaturaPaymentRequestDTO`, `FaturaResponseDTO`) com `ModelMapper` para desacoplar a API do modelo de domínio e controlar a exposição de dados.
* **Configuração OpenAPI/Swagger:** Documentação da API gerada automaticamente, acessível via Swagger UI.

### Frontend (Interface Web Simples)

* **Listagem de Clientes:** Exibe nome, CPF, idade (calculada), status de bloqueio, limite de crédito. Botão para ver faturas do cliente.
* **Cadastro de Clientes:** Formulário para adicionar novos clientes.
* **Faturas do Cliente:** Lista faturas (valor, vencimento, status, pagamento). Botão para registrar pagamento para faturas não pagas.
* **Interação com API:** Utiliza `fetch` API para comunicação assíncrona com o backend.

## 🧪 Testes

O projeto inclui testes automatizados para garantir a qualidade e a funcionalidade do código.

* **Como Rodar os Testes:**
  
  * Certifique-se de que o **Docker Desktop esteja rodando**.
  
  * Navegue até a pasta `backend/` no seu terminal.
  
  * Execute:
    
    ```bash
    ./mvnw clean install
    ```
    
    *(Isso vai rodar todos os testes e, se passarem, gerará o JAR e o instalará no seu repositório local Maven).*

* **Tipos de Testes:**
  
  * **Testes Unitários:**
    * **`ClienteServiceTest`**: Testa a lógica de negócio do serviço de Cliente de forma isolada, mockando repositórios e ModelMapper. Inclui testes para cadastro, listagem, atualização (com regras de bloqueio) e a lógica do job agendado.
    * **`FaturaServiceTest`**: Testa a lógica de negócio do serviço de Fatura de forma isolada, mockando repositórios e ModelMapper. Inclui testes para listagem, registro de pagamento e listagem de faturas atrasadas.
    * **`ValidCharStatusValidatorTest`**: Testa o validador customizado para campos `Character`, garantindo que apenas valores permitidos sejam aceitos.
  * **Testes de Integração:**
    * **`PagamentosApplicationTests`**: Um teste de contexto completo (`@SpringBootTest`) que verifica se a aplicação Spring Boot consegue iniciar corretamente com a configuração de banco de dados (usando Testcontainers).

## 💡 Melhorias Futuras / Pontos a Desenvolver

Com mais tempo e recursos, as seguintes funcionalidades e aprimoramentos seriam implementados:

* **Autenticação e Autorização (Spring Security):** Implementar um sistema de login, gerenciamento de usuários e controle de acesso baseado em papéis (RBAC) para proteger os endpoints da API.
* **Tratamento de Exceções Mais Granular:** Criar exceções customizadas mais específicas para cada tipo de erro de negócio (ex: `ClienteJaExisteException`, `LimiteDeCreditoExcedidoException`), e mapeá-las para respostas HTTP mais detalhadas no `GlobalExceptionHandler`.
* **Paginação e Filtragem:** Adicionar suporte a paginação e filtros complexos nas listagens de clientes e faturas para lidar com grandes volumes de dados de forma eficiente.
* **Auditoria/Logging:** Configurar logs mais detalhados e um sistema de auditoria para rastrear alterações importantes nos dados.
