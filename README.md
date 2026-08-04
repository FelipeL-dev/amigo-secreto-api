![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED)
![License](https://img.shields.io/badge/License-MIT-blue)
# 🎁 Amigo Secreto API

API REST desenvolvida com **Spring Boot** para gerenciamento de sorteios de Amigo Secreto. O sistema permite criar grupos, convidar participantes, realizar sorteios automáticos e enviar os resultados por e-mail de forma segura.

---

## ✨ Funcionalidades

- Cadastro e autenticação de usuários
- Verificação de e-mail
- Recuperação de senha por código enviado por e-mail
- Autenticação com JWT (Access Token + Refresh Token)
- Gerenciamento de grupos
- Convite para grupos através de link
- Criação e realização de sorteios
- Envio automático do resultado do sorteio por e-mail
- Documentação automática com Swagger/OpenAPI
- Docker e Docker Compose

---

## 🛠 Tecnologias

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Hibernate
- JWT (JJWT)
- Spring Mail
- Spring Validation
- Lombok
- SpringDoc OpenAPI (Swagger)
- Docker
- Docker Compose
- Maven

---

## 🚀 Como executar

### Utilizando Docker (Recomendado)

Clone o repositório:

```bash
git clone https://github.com/FelipeL-dev/amigo-secreto-api.git
cd amigo-secreto-api
```

Crie um arquivo `.env` baseado no `.env.example`.

Depois execute:

```bash
docker compose up --build
```

A API ficará disponível em:

```
http://localhost:8080
```

---

### Executando localmente

#### Pré-requisitos

- Java 21+
- Maven
- PostgreSQL

Crie um banco de dados chamado:

```sql
CREATE DATABASE amigo_secreto;
```

Configure as variáveis de ambiente:

```
DB_URL
DB_USERNAME
DB_PASSWORD

JWT_SECRET

EMAIL_USERNAME
EMAIL_PASSWORD
```

Depois execute:

```bash
./mvnw spring-boot:run
```

---

## 📖 Documentação da API

Após iniciar a aplicação:

**Swagger UI**

```
http://localhost:8080/swagger-ui/index.html
```

**OpenAPI JSON**

```
http://localhost:8080/v3/api-docs
```

---

## 📁 Estrutura do Projeto

```
src
├── controllers
├── dtos
├── entities
├── enums
├── exceptions
├── handlers
├── repositories
├── security
└── services
```

---

## 🔒 Regras de Negócio

- Um sorteio precisa ter pelo menos dois participantes.
- Nenhum participante pode sortear a si mesmo.
- Cada participante sorteia exatamente uma pessoa.
- Apenas o dono do grupo pode realizar ou finalizar um sorteio.
- Os resultados são enviados automaticamente por e-mail.
- Um usuário pode participar de vários grupos.

---

## 📄 Licença

Projeto desenvolvido para fins de estudo e portfólio.

---

## 👨‍💻 Autor

**Felipe Lopes**

GitHub: https://github.com/FelipeL-dev
