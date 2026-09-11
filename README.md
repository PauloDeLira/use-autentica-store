# Use Autêntica

Plataforma web para a Use Autêntica, uma loja de roupas real, funcionando
como vitrine digital com gerenciamento administrativo de produtos e
estoque. O cliente navega pelo catálogo e inicia a compra pelo WhatsApp;
não há checkout nem gateway de pagamento no MVP.

O projeto tem dois objetivos: resolver uma necessidade real da loja e
servir como portfólio técnico (Java/Spring, React, PostgreSQL).

## Funcionalidades

**Loja (público)**
- Home com categorias e produtos em destaque (carrossel quando há mais de 4)
- Catálogo com filtro por categoria, tamanho e cor, e ordenação por preço
- Página de produto com galeria de imagens e variações disponíveis
- Compra via WhatsApp: seleciona tamanho/cor, quantidade, e abre uma
  conversa com a mensagem pronta

**Administração** (`/admin`, autenticado)
- Login com JWT
- Dashboard com indicadores da loja (produtos, ativos, sem estoque, itens
  disponíveis)
- CRUD de produtos, com cadastro de variações (tamanho/cor/estoque) e
  imagens direto no formulário
- CRUD de categorias
- Controle de estoque por variação, com aviso de estoque baixo

## Tecnologias

**Backend:** Java 21, Spring Boot 4, Spring Web, Spring Data JPA, Spring
Security, JWT (jjwt), Bean Validation, Flyway, PostgreSQL, JUnit, Mockito,
Testcontainers, springdoc-openapi (Swagger UI).

**Frontend:** React 19, TypeScript, Vite, React Router, CSS Modules,
Fetch API nativa.

**Infraestrutura:** Docker, Docker Compose, Git.

Sem Lombok e sem MapStruct — entidades e mapeamento DTO↔entidade escritos
à mão, por decisão explícita (fins didáticos de portfólio).

## Arquitetura

```
Frontend React  →  REST API (Spring Boot)  →  PostgreSQL
```

Backend organizado por camada (`controller` → `service` → `repository`),
com DTOs próprios para request/response — nunca expõe entidades JPA pela
API. Regras de negócio ficam nos services, nunca nos controllers.
Tratamento de erros centralizado em um `@RestControllerAdvice` único
(`GlobalExceptionHandler`), que padroniza toda resposta de erro no formato:

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "PRODUCT_NOT_FOUND",
  "message": "Produto não encontrado",
  "path": "/api/products/10"
}
```

Imagens de produto são armazenadas em disco local através de uma
abstração (`ImageStorageService`), permitindo trocar por um serviço de
armazenamento em nuvem no futuro sem reescrever a camada de negócio.

### Estrutura do projeto

```
use-autentica-store/
├── backend/
│   └── src/main/java/br/com/useautentica/backend/
│       ├── config/       # CORS, seeders (admin, tamanhos), OpenAPI, upload estático
│       ├── controller/
│       │   ├── admin/       # protegidos por ROLE_ADMIN
│       │   └── publicapi/   # acesso público
│       ├── dto/          # requests/responses, um subpacote por domínio
│       ├── entity/
│       ├── exception/    # GlobalExceptionHandler e exceções específicas
│       ├── repository/
│       ├── security/     # JWT, filtros, UserDetailsService
│       ├── service/      # regras de negócio
│       └── storage/      # abstração de armazenamento de imagens
├── frontend/
│   └── src/
│       ├── admin/         # painel administrativo (rotas, layout, páginas)
│       ├── api/            # cliente HTTP e chamadas por recurso
│       ├── auth/            # sessão do admin (contexto + localStorage)
│       ├── components/      # componentes públicos reutilizáveis
│       ├── pages/            # páginas públicas (Home, Catálogo, Detalhe)
│       ├── types/             # tipos TS espelhando os DTOs do backend
│       └── utils/              # WhatsApp, formatação de preço, etc.
└── docs/
    ├── historico-do-projeto.md  # histórico completo de decisões e status por sprint
    ├── diagrama-er.md
    ├── requisitos.md
    └── identidade-visual/   # manual de marca (cores, tipografia, logo)
```

## Como executar

Pré-requisitos: Docker e Docker Compose. Não é necessário ter Java, Maven
ou Node instalados — o backend roda inteiramente em containers; o
frontend só precisa de Node se você for rodar o `npm run dev` fora de
container (mais simples para hot-reload).

### Backend

```bash
cd backend
cp .env.example .env   # ajuste os valores, especialmente as senhas
docker compose up --build
```

A API sobe em `http://localhost:8080`. `GET /api/health` confirma que
subiu. Um usuário `ADMIN` é criado automaticamente no primeiro start, com
as credenciais de `ADMIN_EMAIL`/`ADMIN_PASSWORD` do `.env`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Abre em `http://localhost:5173`. O `frontend/.env` já vem no repositório
com `VITE_API_BASE_URL=http://localhost:8080` (não contém segredo) — ajuste
`VITE_WHATSAPP_NUMBER` se for testar a compra via WhatsApp com outro número.

## Como configurar

Veja `backend/.env.example` para a lista completa de variáveis (banco,
JWT, credenciais do admin, pasta de upload de imagens). Nunca versionar o
`.env` real — ele já está no `.gitignore`.

## Como executar os testes

Os testes de integração sobem um PostgreSQL real e isolado via
**Testcontainers** — não usam mais o banco de dev. Não há Maven instalado
localmente neste projeto; os testes rodam via container:

```bash
cd backend
docker run --rm \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v "$(pwd):/app" -w /app \
  -e TESTCONTAINERS_RYUK_DISABLED=true \
  -e TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal \
  maven:3.9-eclipse-temurin-21 mvn test
```

As duas variáveis de ambiente contornam particularidades do Docker
Desktop (Windows/Mac) ao rodar Testcontainers de dentro de outro
container — sem elas, o Testcontainers não consegue alcançar o Postgres
que ele mesmo sobe. Em CI com Docker nativo (Linux) normalmente não são
necessárias.

Documentação interativa da API (Swagger UI) em
`http://localhost:8080/swagger-ui/index.html` com o backend rodando.

## Decisões técnicas relevantes

- **Size e Color são entidades**, não enums — permite cadastrar novos
  tamanhos/cores pela área admin sem precisar de deploy.
- **Soft delete para variações de produto** (preserva histórico); **hard
  delete para produtos sem variação** (bloqueado com 422 se houver
  variações — desative em vez de excluir).
- **Preço único por produto**, não varia por variação de tamanho/cor.
- **Sem carrinho, checkout ou gateway de pagamento** no MVP — a compra é
  formalizada via WhatsApp, com a mensagem já pronta.
- **JWT próprio** (biblioteca `jjwt`), sem servidor de autorização externo;
  token de acesso único (~24h), sem refresh token.

O histórico completo de decisões, trade-offs e status de cada etapa do
desenvolvimento está em [`docs/historico-do-projeto.md`](docs/historico-do-projeto.md).

## Roadmap

O MVP não inclui carrinho, checkout, gateway de pagamento ou gestão de
pedidos — só catálogo + WhatsApp. Evoluções futuras estão descritas em
detalhe em `docs/historico-do-projeto.md` (seção "Roadmap futuro" e "Backlog
pós-MVP"), incluindo:

- **V2:** pedidos, histórico, dashboard mais completo, métricas de
  intenção de compra a partir de cliques no botão do WhatsApp.
- **V3:** infraestrutura de produção (CI/CD, deploy, cloud storage,
  monitoramento).
- **V4:** e-commerce completo (carrinho, checkout, gateway de pagamento).
