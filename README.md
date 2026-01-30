# 🎁 Amigo Secreto API

API REST completa para gerenciamento de sorteios de Amigo Secreto (Secret Santa), desenvolvida com Spring Boot.

## 📋 Sobre o Projeto

Esta API permite criar e gerenciar eventos de Amigo Secreto de forma organizada e automatizada. O sistema possibilita o cadastro de pessoas, criação de grupos, realização de sorteios automáticos e consulta de resultados, garantindo que ninguém tire a si mesmo e que todos participem do sorteio.

## 🚀 Tecnologias Utilizadas

- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.3** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados relacional
- **Hibernate** - ORM (Object-Relational Mapping)
- **Spring Validation** - Validação de dados
- **Lombok** - Redução de boilerplate code
- **SpringDoc OpenAPI 2.8.9** - Documentação automática da API (Swagger)
- **Maven** - Gerenciamento de dependências

## 📦 Pré-requisitos

- Java JDK 21 ou superior
- Maven 3.6 ou superior
- PostgreSQL 12 ou superior

## 🔧 Instalação e Configuração

### 1. Clone o repositório
```bash
git clone https://github.com/COKINHOxo/amigo-secreto-api.git
cd amigo-secreto-api
```

### 2. Configure o banco de dados
Crie um banco de dados PostgreSQL chamado `amigo_secreto`:
```sql
CREATE DATABASE amigo_secreto;
```

### 3. Configure as credenciais
Edite o arquivo `src/main/resources/application.properties` com suas credenciais:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/amigo_secreto
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 4. Compile e execute
```bash
# Usando Maven Wrapper
./mvnw clean install
./mvnw spring-boot:run

# Ou usando Maven instalado
mvn clean install
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`

## 📚 Documentação da API

Após iniciar a aplicação, acesse a documentação interativa do Swagger:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🎯 Funcionalidades

### ✅ Gerenciamento de Pessoas
- Cadastro de participantes com nome e email
- Listagem, busca, atualização e exclusão de pessoas
- Associação de pessoas a grupos

### ✅ Gerenciamento de Grupos
- Criação de grupos para organizar sorteios
- Listagem de grupos e grupos já sorteados
- Atualização e exclusão de grupos
- Visualização de pessoas por grupo

### ✅ Sistema de Sorteio
- Criação de sorteios vinculados a grupos
- Realização automática de sorteios
- Garantia de que ninguém sorteia a si mesmo
- Controle de status (EM_ANDAMENTO, FINALIZADO)
- Validação de número mínimo de participantes (2 pessoas)
- Requisito de número par de participantes

### ✅ Resultados
- Consulta de resultados por sorteio
- Visualização de quem tirou quem
- Histórico completo de sorteios realizados

## 📝 Endpoints da API

### 👥 Pessoas (`/api/pessoas`)

#### Listar todas as pessoas
```http
GET /api/pessoas
```

#### Buscar pessoa por ID
```http
GET /api/pessoas/{id}
```

#### Criar pessoa
```http
POST /api/pessoas
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@example.com",
  "grupoIds": [1, 2]
}
```

#### Atualizar pessoa
```http
PUT /api/pessoas/{id}
Content-Type: application/json

{
  "nome": "João da Silva",
  "email": "joao.silva@example.com",
  "grupoIds": [1]
}
```

#### Deletar pessoa
```http
DELETE /api/pessoas/{id}
```

---

### 👨‍👩‍👧‍👦 Grupos (`/api/grupos`)

#### Listar todos os grupos
```http
GET /api/grupos
```

#### Listar grupos já sorteados
```http
GET /api/grupos/sorteados
```

#### Buscar grupo por ID
```http
GET /api/grupos/{id}
```

#### Listar pessoas de um grupo
```http
GET /api/grupos/{grupoId}/pessoas
```

#### Criar grupo
```http
POST /api/grupos
Content-Type: application/json

{
  "nome": "Família Silva"
}
```

#### Atualizar grupo
```http
PUT /api/grupos/{id}
Content-Type: application/json

{
  "nome": "Família Silva - Natal 2024",
  "sorteado": false
}
```

#### Adicionar pessoa ao grupo
```http
POST /api/grupos/{grupoId}/pessoas/{pessoaId}
```

#### Deletar grupo
```http
DELETE /api/grupos/{id}
```

---

### 🎲 Sorteios (`/api/sorteios`)

#### Listar todos os sorteios
```http
GET /api/sorteios
```

#### Buscar sorteio por ID
```http
GET /api/sorteios/{id}
```

#### Criar sorteio
```http
POST /api/sorteios
Content-Type: application/json

{
  "grupoId": 1
}
```

#### Realizar sorteio automaticamente
```http
POST /api/sorteios/{id}/realizar
```
> Este endpoint realiza o sorteio e o finaliza automaticamente

#### Finalizar sorteio manualmente
```http
PATCH /api/sorteios/{id}/finalizar
```

#### Deletar sorteio
```http
DELETE /api/sorteios/{id}
```

---

### 📊 Resultados (`/api/resultadosorteio`)

#### Buscar resultado por ID
```http
GET /api/resultadosorteio/{id}
```

#### Buscar todos os resultados de um sorteio
```http
GET /api/resultadosorteio/sorteio/{sorteioId}
```

#### Criar resultado manualmente
```http
POST /api/resultadosorteio
Content-Type: application/json

{
  "sorteio_id": 1,
  "sorteador_id": 2,
  "sorteado_id": 3
}
```

#### Deletar resultado
```http
DELETE /api/resultadosorteio/{id}
```

## 🏗️ Estrutura do Projeto

```
amigo-secreto-api/
├── src/
│   ├── main/
│   │   ├── java/com/projeto/amigo/secreto/
│   │   │   ├── controllers/          # Controladores REST
│   │   │   │   ├── PessoaController.java
│   │   │   │   ├── GrupoController.java
│   │   │   │   ├── SorteioController.java
│   │   │   │   └── ResultadoSorteioController.java
│   │   │   ├── service/              # Lógica de negócio
│   │   │   │   ├── PessoaService.java
│   │   │   │   ├── GrupoService.java
│   │   │   │   ├── SorteioService.java
│   │   │   │   └── ResultadoSorteioService.java
│   │   │   ├── entities/             # Entidades JPA
│   │   │   │   ├── Pessoa.java
│   │   │   │   ├── Grupo.java
│   │   │   │   ├── Sorteio.java
│   │   │   │   └── ResultadoSorteio.java
│   │   │   ├── repositories/         # Interfaces de dados
│   │   │   ├── dtos/                 # Data Transfer Objects
│   │   │   └── enums/                # Enumerações
│   │   │       └── StatusSorteio.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## 🔄 Fluxo de Uso

1. **Criar Pessoas**: Cadastre os participantes do sorteio
2. **Criar Grupo**: Organize as pessoas em um grupo
3. **Adicionar Pessoas ao Grupo**: Associe as pessoas ao grupo criado
4. **Criar Sorteio**: Vincule o sorteio ao grupo
5. **Realizar Sorteio**: Execute o sorteio automático
6. **Consultar Resultados**: Veja quem tirou quem

## 🔐 Regras de Negócio

- ✅ Um sorteio precisa de no mínimo 2 pessoas
- ✅ O número de participantes deve ser par
- ✅ Ninguém pode sortear a si mesmo
- ✅ Cada pessoa sorteia exatamente uma outra pessoa
- ✅ Um sorteio não pode ser realizado novamente se já estiver finalizado
- ✅ Pessoas podem pertencer a múltiplos grupos
- ✅ Grupos marcados como sorteados não permitem novo sorteio

## 🧪 Testes

Execute os testes com:
```bash
./mvnw test
```

## 🛠️ Desenvolvimento

### Modo de Desenvolvimento
O projeto utiliza Spring DevTools para reload automático durante o desenvolvimento.

### CORS
A API está configurada para aceitar requisições de `http://localhost:5173` (útil para frontends React/Vue/Angular em desenvolvimento).

## 🤝 Contribuindo

Contribuições são bem-vindas! Siga os passos:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 📄 Licença

Este é um projeto pessoal desenvolvido para fins de aprendizado e portfólio.

## 👨‍💻 Autor

**COKINHOxo**
- GitHub: [@COKINHOxo](https://github.com/COKINHOxo)
- Projeto: [amigo-secreto-api](https://github.com/COKINHOxo/amigo-secreto-api)

## 🙏 Agradecimentos

Projeto desenvolvido como parte do aprendizado em desenvolvimento de APIs REST com Spring Boot.

---

⭐️ Se este projeto foi útil para você, considere dar uma estrela no repositório!
