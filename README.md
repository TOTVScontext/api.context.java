# news-api

API RESTful de notícias — Java 17, Spring Boot 3, Maven e Oracle (JDBC), seguindo a estrutura MVC adotada em sala (`Controller`, `Model/Dao`, `Model/Dto`).

## Estrutura

```
br.com.fiap
├── NewsApiApplication
├── Config                 CORS e headers de segurança
├── Controller             NewsController (endpoints REST)
├── Excecao                Exceções de domínio e ApiExceptionHandler
└── Model
    ├── Dao                IDAO, ConnectionFactory, NewsDao
    ├── Dto                News, NewsRequest, NewsPage, ErrorResponse
    └── Service            NewsService (validações e orquestração)
```

## Banco de dados

Execute `database/schema.sql` no Oracle (tabela `news`).

## Execução

```bash
mvn spring-boot:run
```

Variáveis opcionais: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `CORS_ALLOWED_ORIGINS`, `PORT`.

## Endpoints (`/api/news`)

| Método | Query | Descrição |
|---|---|---|
| GET | `?action=list&page=1&page_size=20` | Lista paginada (máx. 50 por página) |
| GET | `?action=get&id=<id>` | Busca por ID |
| POST | `?action=create` | Cria (`title` e `content` obrigatórios; `subtitle` e `redirection` opcionais) |
| PUT | `?action=update&id=<id>` | Atualiza |
| DELETE | `?action=delete&id=<id>` | Remove (204) |

Erros: `{ "error": "mensagem" }` com 400, 404, 405 ou 500.
