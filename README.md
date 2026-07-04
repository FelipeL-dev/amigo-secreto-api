# 🎁 Amigo Secreto API

API REST completa para gerenciamento de sorteios de Amigo Secreto (Secret Santa), desenvolvida com Spring Boot.

## 📋 Sobre o Projeto

Esta API permite criar e gerenciar eventos de Amigo Secreto de forma organizada e automatizada. O sistema possibilita o cadastro de usuários, criação de grupos, convite de participantes via link, realização de sorteios automáticos e envio dos resultados por email.

## 🚀 Tecnologias Utilizadas

- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.3** - Framework principal
- **Spring Security** - Autenticação e autorização
- **JWT (JJWT 0.12.6)** - Autenticação stateless
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados relacional
- **Hibernate** - ORM (Object-Relational Mapping)
- **Spring Mail** - Envio de emails via SMTP
- **Spring Validation** - Validação de dados
- **Lombok** - Redução de boilerplate code
- **SpringDoc OpenAPI 2.8.9** - Documentação automática (Swagger)
- **Maven** - Gerenciamento de dependências

## 📦 Pré-requisitos

- Java JDK 21 ou superior
- Maven 3.6 ou superior
- PostgreSQL 12 ou superior
- Conta Gmail com senha de app gerada

## 🔧 Instalação e Configuração

### 1. Clone o repositório
```bash
git clone https://github.com/FelipeL-dev/amigo-secreto-api.git
cd amigo-secreto-api
```

### 2. Configure o banco de dados
Crie um banco de dados PostgreSQL chamado `amigo_secreto`:
```sql
CREATE DATABASE amigo_secreto;
```

### 3. Configure as credenciais
Edite o arquivo `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/amigo_secreto
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

jwt.secret=sua-chave-secreta-com-pelo-menos-32-caracteres
jwt.expiration=86400000

spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_de_app_gmail
```

### 4. Compile e execute
```bash
./mvnw clean install
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`

## 📚 Documentação da API

Após iniciar a aplicação, acesse:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🎯 Funcionalidades

### ✅ Autenticação e Usuários
- Registro de usuários com criação automática de perfil de participante
- Login com geração de access token e refresh token JWT
- Renovação de token via refresh token
- Edição de perfil com renovação automática de tokens

### ✅ Gerenciamento de Grupos
- Criação de grupos com ownership automático
- Geração de link de convite para adicionar participantes
- Listagem dos grupos do usuário logado
- Atualização e exclusão de grupos (somente pelo dono)
- Visualização de pessoas por grupo

### ✅ Sistema de Sorteio
- Criação de sorteios vinculados a grupos
- Realização automática do sorteio com algoritmo circular
- Garantia de que ninguém sorteia a si mesmo
- Controle de status (EM_ANDAMENTO, FINALIZADO)
- Validação de número mínimo de participantes (2 pessoas)
- Envio automático de resultados por email para cada participante
- Operações protegidas por ownership (somente o dono do grupo pode sortear)

### ✅ Resultados
- Consulta de resultados por sorteio
- Resultados enviados por email para cada participante

## 📝 Endpoints da API

### 🔐 Autenticação (`/auth`)

#### Registrar usuário
```http
POST /auth/register
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@example.com",
  "password": "123456"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "123456"
}
```

#### Renovar token
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "uuid-do-refresh-token"
}
```

---

### 👤 Usuário (`/api/usuarios`)

#### Buscar perfil do usuário logado
```http
GET /api/usuarios/me
Authorization: Bearer {token}
```

#### Atualizar perfil
```http
PUT /api/usuarios/me
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "João da Silva",
  "email": "joao.silva@example.com"
}
```

---

### 👨‍👩‍👧‍👦 Grupos (`/api/grupos`)

#### Listar meus grupos
```http
GET /api/grupos/meus
Authorization: Bearer {token}
```

#### Listar todos os grupos (admin)
```http
GET /api/grupos
Authorization: Bearer {token}
```

#### Buscar grupo por ID
```http
GET /api/grupos/{id}
Authorization: Bearer {token}
```

#### Criar grupo
```http
POST /api/grupos
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "Família Silva"
}
```

#### Atualizar grupo
```http
PUT /api/grupos/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "Família Silva - Natal 2025"
}
```

#### Gerar link de convite
```http
POST /api/grupos/{id}/convite
Authorization: Bearer {token}
```

#### Entrar no grupo via convite
```http
POST /api/grupos/entrar/{token}
Authorization: Bearer {token}
```

#### Deletar grupo
```http
DELETE /api/grupos/{id}
Authorization: Bearer {token}
```

---

### 🎲 Sorteios (`/api/sorteios`)

#### Listar todos os sorteios
```http
GET /api/sorteios
Authorization: Bearer {token}
```

#### Buscar sorteio por ID
```http
GET /api/sorteios/{id}
Authorization: Bearer {token}
```

#### Criar sorteio
```http
POST /api/sorteios
Authorization: Bearer {token}
Content-Type: application/json

{
  "grupoId": 1
}
```

#### Realizar sorteio
```http
POST /api/sorteios/{id}/realizar
Authorization: Bearer {token}
```

#### Finalizar sorteio
```http
PATCH /api/sorteios/{id}/finalizar
Authorization: Bearer {token}
```

#### Deletar sorteio
```http
DELETE /api/sorteios/{id}
Authorization: Bearer {token}
```

---

### 📊 Resultados (`/api/resultadosorteio`)

#### Buscar resultado por ID
```http
GET /api/resultadosorteio/{id}
Authorization: Bearer {token}
```

#### Buscar resultados de um sorteio
```http
GET /api/resultadosorteio/sorteio/{sorteioId}
Authorization: Bearer {token}
```

## 🏗️ Estrutura do Projeto
```
amigo-secreto-api/
├── src/main/java/com/projeto/amigo/secreto/
│   ├── controllers/        # Controladores REST
│   ├── service/            # Lógica de negócio
│   ├── entities/           # Entidades JPA
│   ├── repositories/       # Interfaces de dados
│   ├── dtos/               # Data Transfer Objects
│   ├── enums/              # Enumerações
│   ├── exceptions/         # Exceções customizadas
│   ├── handlers/           # Handler global de exceções
│   └── security/           # Configuração JWT e Spring Security
└── pom.xml
```

## 🔄 Fluxo de Uso

1. **Registrar**: Crie uma conta via `/auth/register`
2. **Login**: Autentique-se e guarde o access token e refresh token
3. **Criar Grupo**: Crie um grupo — você é adicionado automaticamente como dono e participante
4. **Convidar**: Gere um link de convite e compartilhe com os participantes
5. **Entrar no Grupo**: Participantes acessam o link e entram no grupo
6. **Criar Sorteio**: Vincule um sorteio ao grupo
7. **Realizar Sorteio**: Execute o sorteio — cada participante recebe o resultado por email

## 🔐 Regras de Negócio

- ✅ Um sorteio precisa de no mínimo 2 pessoas
- ✅ Ninguém sorteia a si mesmo
- ✅ Cada pessoa sorteia exatamente uma outra pessoa
- ✅ Um sorteio finalizado não pode ser realizado novamente
- ✅ Somente o dono do grupo pode sortear, finalizar ou deletar
- ✅ Pessoas podem pertencer a múltiplos grupos
- ✅ Resultados são enviados por email automaticamente

## 🛠️ Desenvolvimento

O projeto utiliza Spring DevTools para reload automático durante o desenvolvimento.

## 📄 Licença

Este é um projeto pessoal desenvolvido para fins de aprendizado e portfólio.

## 👨‍💻 Autor

**Felipe Lopes**
- GitHub: [@FelipeL-dev](https://github.com/FelipeL-dev)

---

⭐️ Se este projeto foi útil para você, considere dar uma estrela no repositório!
