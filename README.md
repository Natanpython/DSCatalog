# Catálogo de produtos — Java Spring Expert

API REST de estudo para gerenciamento de produtos e categorias, desenvolvida durante o aprendizado de Java e Spring Boot.

> **Em desenvolvimento:** o projeto acompanha a evolução dos estudos. Funcionalidades, validações e respostas da API ainda podem mudar. Este README descreve a estrutura presente no código, sem representar uma versão final pronta para produção.

## Tecnologias

- Java 21, conforme configuração do projeto.
- Spring Boot 4.1.1.
- Spring Web MVC.
- Spring Data JPA e Hibernate.
- Jakarta Bean Validation.
- Banco H2 em memória.
- Maven, com Maven Wrapper incluído.
- JUnit e suporte de testes do Spring Boot.

## Estado atual

- Endpoints de consulta, cadastro, atualização e exclusão de produtos e categorias.
- Consultas paginadas e ordenação.
- Relacionamento muitos-para-muitos entre produtos e categorias.
- DTOs para entrada e saída de dados.
- Categorias incluídas nas consultas de produtos.
- Validações de campos e tratamento centralizado de exceções.
- Dados de exemplo carregados por `src/main/resources/import.sql`.
- Entidades de usuários e perfis (`User` e `Role`) modeladas; autenticação e endpoints de usuários ainda não estão implementados.

## Organização

```text
src/main/java/com/estudo/aula/
├── controller/          # Endpoints REST
│   └── handlers/        # Tratamento de exceções HTTP
├── dto/                 # Objetos de entrada e saída
├── entities/            # Entidades e modelos
├── repositories/        # Acesso ao banco de dados
└── services/            # Operações e regras da aplicação

src/main/resources/
├── application.properties
├── application-test.properties
└── import.sql

src/test/java/           # Testes automatizados
```

## Como executar

Tenha um JDK compatível com Java 21 e configure `JAVA_HOME`. Na primeira execução, o Maven precisa de acesso à internet para baixar as dependências.

Na pasta que contém o `pom.xml`, execute:

**Windows — PowerShell:**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**

```bash
./mvnw spring-boot:run
```

Também é possível executar a classe `AulaApplication` pela IDE. Se o Wrapper apresentar um problema no ambiente local, uma instalação compatível do Maven pode ser utilizada com `mvn spring-boot:run`.

A URL local padrão é `http://localhost:8080`. O perfil `test` está ativo em `application.properties` e utiliza o H2 em memória, sem necessidade de instalar um banco externo.

### Console H2

Com a aplicação em execução, abra `http://localhost:8080/h2-console`:

| Campo | Valor |
| --- | --- |
| JDBC URL | `jdbc:h2:mem:testdb` |
| User Name | `sa` |
| Password | Deixar em branco |

Os dados são temporários: alterações feitas durante a execução não são preservadas após encerrar a aplicação. O `import.sql` serve apenas para povoar o banco com exemplos de estudo.

## Endpoints

As rotas atuais não exigem autenticação.

| Método | Produtos | Categorias | Operação |
| --- | --- | --- | --- |
| GET | `/products` | `/categories` | Listar com paginação |
| GET | `/products/{id}` | `/categories/{id}` | Consultar por ID |
| POST | `/products` | `/categories` | Cadastrar |
| PUT | `/products/{id}` | `/categories/{id}` | Atualizar |
| DELETE | `/products/{id}` | `/categories/{id}` | Excluir |

### Paginação

```http
GET /products?page=0&size=10&sort=name,asc
```

- `page`: índice da página, começando em zero.
- `size`: quantidade de itens por página.
- `sort`: campo e direção da ordenação (`asc` ou `desc`).

### Cadastro de categoria

Envie para `POST /categories`, com `Content-Type: application/json`:

```json
{
  "name": "Acessórios"
}
```

O nome é obrigatório e deve ter de 3 a 80 caracteres. Para atualizar, envie o mesmo formato para `PUT /categories/{id}`.

### Cadastro de produto

Envie para `POST /products`, com `Content-Type: application/json`:

```json
{
  "name": "Teclado mecânico",
  "description": "Teclado mecânico com conexão USB.",
  "price": 250.0,
  "imgUrl": "https://example.com/teclado.jpg",
  "date": "2026-10-24T12:00:00Z",
  "categories": [
    { "id": 3 }
  ]
}
```

Use IDs de categorias já cadastradas. Para atualizar, envie os campos para `PUT /products/{id}`; a coleção enviada substitui as associações anteriores do produto.

Validações atualmente declaradas no DTO:

- Nome obrigatório, com 3 a 80 caracteres.
- Descrição com pelo menos 10 caracteres, quando informada.
- Preço positivo, quando informado.

A obrigatoriedade de outros campos e a validação das associações ainda podem evoluir. Atualmente, as consultas de produtos preenchem `categories`; as respostas de cadastro e atualização usam um construtor do DTO que não copia essas associações. Consulte o produto por ID para visualizar suas categorias após a gravação.

### Respostas e erros

- Consultas e atualizações bem-sucedidas retornam `200 OK`.
- Cadastros bem-sucedidos retornam `201 Created`, com o cabeçalho `Location`.
- Exclusões bem-sucedidas retornam `204 No Content`.
- O handler converte `ResourceNotFoundException` em `404 Not Found`.
- Erros de validação tratados retornam `422`, com detalhes dos campos.
- `DatabaseException` é convertida em `400 Bad Request`.

Exemplo ilustrativo de erro de validação:

```json
{
  "timestamp": "2026-10-24T12:00:00Z",
  "status": 422,
  "error": "Dados invalidos",
  "path": "/categories",
  "errors": [
    {
      "fieldName": "name",
      "message": "Nome precisa ter de 3 a 80 caracteres."
    }
  ]
}
```

O tratamento de erros ainda está em evolução; os códigos acima descrevem os caminhos previstos no código, não uma cobertura completa de todos os cenários de falha.

## Testes

**Windows:**

```powershell
.\mvnw.cmd test
```

**Linux / macOS:**

```bash
./mvnw test
```

O teste atual verifica o carregamento do contexto do Spring. Ainda não há cobertura específica das operações HTTP, validações e cenários de erro. Os exemplos deste README foram conferidos com o código, mas não executados como parte da criação da documentação.

## Continuidade dos estudos

- Criar consultas personalizadas com JPQL e otimizar o carregamento das categorias.
- Ampliar os testes das operações e dos cenários de erro.
- Evoluir validações e padronização das respostas conforme o desenvolvimento.
- Atualizar esta documentação junto com as próximas funcionalidades.
