# Projeto: Plataforma Web para Loja de Roupas

## 1. Contexto

Desenvolver uma aplicação web para uma pequena loja de roupas, inicialmente funcionando como uma **vitrine digital com gerenciamento administrativo de produtos e estoque**.

O projeto possui dois objetivos principais:

1. Resolver uma necessidade real de uma loja de roupas.
2. Servir como projeto de estudo e portfólio para demonstrar conhecimentos de desenvolvimento backend com Java/Spring, frontend com React e banco de dados PostgreSQL.

A aplicação deverá ser desenvolvida de forma incremental, utilizando sprints bem definidas.

A primeira versão será um **MVP**, sem gateway de pagamento, cálculo de frete ou checkout completo.

O cliente poderá visualizar os produtos e iniciar uma compra através do WhatsApp da loja.

O sistema deverá ser arquitetado de forma que funcionalidades futuras possam ser adicionadas sem necessidade de reescrever completamente a aplicação.

---

# 2. Stack tecnológica

## Backend

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* PostgreSQL
* Bean Validation
* Flyway
* JWT
* JUnit
* Mockito
* Testcontainers
* OpenAPI/Swagger

## Frontend

* React
* TypeScript
* React Router
* HTML
* CSS
* Axios ou Fetch API

## Infraestrutura

Inicialmente:

* Git
* GitHub
* Docker
* Docker Compose

A infraestrutura poderá ser aprofundada nas versões futuras.

---

# 3. Arquitetura

Utilizar arquitetura baseada em API REST.

Fluxo principal:

Frontend React
↓
REST API
↓
Spring Boot
↓
PostgreSQL

As imagens dos produtos não devem ser armazenadas diretamente no PostgreSQL como regra geral.

A aplicação deve utilizar uma abstração para armazenamento de imagens, permitindo inicialmente uma implementação simples e posteriormente uma migração para serviços de armazenamento em nuvem.

---

# 4. Conceito do domínio

A aplicação representa uma loja de roupas.

O sistema deverá trabalhar com **produtos e suas variações**, pois uma mesma peça pode possuir diferentes tamanhos, cores e quantidades em estoque.

Exemplo:

Produto:

Camiseta Oversized

Variações:

* Preta / P
* Preta / M
* Preta / G
* Branca / P
* Branca / M
* Branca / G

Cada combinação de características deverá possuir seu próprio controle de estoque.

Evitar modelar estoque simplesmente como um campo `quantity` pertencente diretamente ao produto.

---

# 5. Entidades iniciais

A modelagem deverá considerar, inicialmente:

## User

Representa usuários administrativos.

Campos sugeridos:

* id
* name
* email
* password
* role
* createdAt
* updatedAt

Roles inicialmente:

* ADMIN

---

## Product

Representa uma peça de roupa.

Campos sugeridos:

* id
* name
* description
* price
* active
* category
* createdAt
* updatedAt

---

## Category

Representa a categoria do produto.

Exemplos:

* Camisetas
* Calças
* Vestidos
* Saias
* Acessórios

Campos:

* id
* name
* description
* active

---

## ProductImage

Representa as imagens de um produto.

Campos sugeridos:

* id
* product
* url
* altText
* displayOrder

Um produto poderá possuir várias imagens.

---

## ProductVariant

Representa uma variação específica do produto.

Campos sugeridos:

* id
* product
* size
* color
* sku
* stockQuantity
* active

Exemplo:

Produto: Camiseta Oversized

Variantes:

P / Preta / SKU-CAM-001-P-PT / 5
M / Preta / SKU-CAM-001-M-PT / 10
G / Preta / SKU-CAM-001-G-PT / 4

---

## Size

Representa os tamanhos disponíveis.

Exemplos:

* PP
* P
* M
* G
* GG
* XG

---

## Color

Representa as cores disponíveis.

Exemplos:

* Preto
* Branco
* Azul
* Vermelho

A modelagem poderá ser adaptada conforme as necessidades reais do domínio.

---

# 6. Regras de negócio importantes

Implementar as regras de negócio na camada de serviço, evitando concentrar regras diretamente nos controllers.

### Produtos

* Produto deve possuir nome.
* Produto deve possuir preço maior que zero.
* Produto pode possuir várias imagens.
* Produto pode possuir várias variações.
* Produto pode ser ativado/desativado.
* Produto inativo não deve aparecer no catálogo público.

### Estoque

* Estoque nunca pode ser negativo.
* Cada variação possui estoque independente.
* Uma variação sem estoque deve ser identificada como indisponível.
* O sistema deve evitar operações que resultem em estoque negativo.
* A atualização do estoque deve possuir regras claras e testáveis.

### Catálogo

* Apenas produtos ativos aparecem para clientes.
* Produtos sem estoque continuam podendo aparecer, mas devem ser identificados como indisponíveis.
* O cliente deve conseguir visualizar as variações disponíveis.

---

# 7. Funcionalidades do MVP

## Área pública

### Home

Exibir:

* Nome/logo da loja
* Destaques
* Categorias
* Produtos recentes ou em destaque
* Link para catálogo

### Catálogo

Permitir:

* Listar produtos
* Filtrar por categoria
* Filtrar por tamanho
* Filtrar por cor
* Ordenar por preço
* Visualizar disponibilidade

### Detalhes do produto

Exibir:

* Nome
* Descrição
* Preço
* Imagens
* Cores
* Tamanhos
* Disponibilidade
* Quantidade disponível quando apropriado

### WhatsApp

Criar botão:

"Comprar pelo WhatsApp"

Ao clicar:

1. Cliente seleciona a variação.
2. Cliente informa a quantidade.
3. Frontend monta uma mensagem.
4. Sistema abre o WhatsApp da loja.

Exemplo de mensagem:

"Olá! Tenho interesse no seguinte produto:

Produto: Camiseta Oversized
Cor: Preta
Tamanho: M
Quantidade: 1
Valor: R$ 89,90

Gostaria de verificar a disponibilidade."

A integração inicial não deverá utilizar a API oficial do WhatsApp.

---

# 8. Área administrativa

A área administrativa deverá exigir autenticação.

Funcionalidades:

### Login

* Email
* Senha
* JWT
* Controle de acesso

### Dashboard

Inicialmente apresentar informações simples:

* Total de produtos
* Produtos ativos
* Produtos sem estoque
* Quantidade total de itens disponíveis

### Produtos

Permitir:

* Criar produto
* Editar produto
* Ativar/desativar produto
* Excluir produto quando apropriado
* Cadastrar imagens
* Cadastrar variações
* Definir preços
* Definir estoque

### Categorias

Permitir:

* Criar categoria
* Editar categoria
* Ativar/desativar categoria
* Listar categorias

### Estoque

Permitir:

* Visualizar estoque
* Alterar estoque
* Identificar produtos sem estoque
* Identificar produtos com estoque baixo

---

# 9. API REST

A API deverá seguir boas práticas REST.

Endpoints iniciais esperados:

## Autenticação

POST /api/auth/login

## Produtos públicos

GET /api/products
GET /api/products/{id}

## Produtos administrativos

POST /api/admin/products
PUT /api/admin/products/{id}
DELETE /api/admin/products/{id}
PATCH /api/admin/products/{id}/active

## Imagens

POST /api/admin/products/{id}/images
DELETE /api/admin/products/{id}/images/{imageId}

## Variações

POST /api/admin/products/{id}/variants
PUT /api/admin/products/{id}/variants/{variantId}
DELETE /api/admin/products/{id}/variants/{variantId}

## Categorias

GET /api/categories
POST /api/admin/categories
PUT /api/admin/categories/{id}
PATCH /api/admin/categories/{id}/active

## Estoque

GET /api/admin/inventory
PATCH /api/admin/inventory/{variantId}

Os endpoints podem ser ajustados conforme a implementação e as decisões arquiteturais tomadas durante o desenvolvimento.

---

# 10. DTOs

Não expor diretamente entidades JPA nos controllers.

Utilizar DTOs para:

* Requests
* Responses
* Autenticação
* Produtos
* Categorias
* Variações
* Imagens

Exemplo conceitual:

ProductRequest
ProductResponse
ProductSummaryResponse
ProductVariantRequest
ProductVariantResponse
LoginRequest
LoginResponse

Utilizar Bean Validation nos DTOs de entrada.

---

# 11. Organização do backend

Utilizar uma estrutura organizada por responsabilidade/domínio.

Uma possibilidade:

src/main/java/.../

├── config
├── controller
├── dto
│   ├── product
│   ├── category
│   ├── auth
│   └── inventory
├── entity
├── repository
├── service
├── security
├── exception
└── mapper

A estrutura poderá evoluir para uma organização por feature/domínio caso isso faça mais sentido durante o desenvolvimento.

O objetivo não é criar complexidade arquitetural artificial.

---

# 12. Tratamento de erros

Criar tratamento global de exceções utilizando `@RestControllerAdvice`.

A API deverá retornar respostas padronizadas para erros.

Exemplo:

{
"timestamp": "...",
"status": 404,
"error": "PRODUCT_NOT_FOUND",
"message": "Produto não encontrado",
"path": "/api/products/10"
}

Criar exceções específicas quando fizer sentido.

Exemplos:

* ResourceNotFoundException
* BusinessException
* InvalidStockOperationException

---

# 13. Banco de dados

Utilizar PostgreSQL.

Utilizar Flyway para versionamento das migrations.

Não depender de `ddl-auto=create` em ambientes reais.

As migrations deverão ser versionadas no Git.

Exemplo:

V1__create_users.sql
V2__create_categories.sql
V3__create_products.sql
V4__create_product_variants.sql
V5__create_product_images.sql

A estrutura real deverá acompanhar a evolução do domínio.

---

# 14. Testes

Os testes devem fazer parte do desenvolvimento e não ser adicionados somente ao final.

Implementar:

### Unitários

Testar principalmente:

* Services
* Regras de estoque
* Validações
* Regras de negócio

### Integração

Utilizar Testcontainers para testar integração com PostgreSQL.

Testar:

* Persistência
* Repositories
* Migrations
* Endpoints importantes

O objetivo não é atingir uma porcentagem arbitrária de cobertura, mas testar principalmente comportamentos relevantes do sistema.

---

# 15. Documentação

Utilizar OpenAPI/Swagger.

Documentar:

* Endpoints
* Parâmetros
* Requests
* Responses
* Erros
* Autenticação

O README do projeto deverá explicar:

* Objetivo
* Funcionalidades
* Tecnologias
* Arquitetura
* Como executar
* Como configurar
* Como executar testes
* Estrutura do projeto
* Decisões técnicas relevantes
* Roadmap

---

# 16. Sprints

O desenvolvimento deverá seguir as seguintes sprints.

---

# SPRINT 01 — Planejamento e modelagem

Objetivo:

Definir claramente o domínio antes de começar a implementação.

Tarefas:

* Definir requisitos funcionais.
* Definir requisitos não funcionais.
* Definir entidades.
* Definir relacionamentos.
* Definir regras de negócio.
* Criar modelo inicial do banco.
* Definir endpoints.
* Definir arquitetura do projeto.
* Criar repositório Git.
* Criar README inicial.

Entregáveis:

* Diagrama ER.
* Modelo inicial do domínio.
* Lista de requisitos.
* Lista inicial de endpoints.
* Estrutura inicial dos projetos backend/frontend.

Não implementar funcionalidades ainda sem necessidade.

---

# SPRINT 02 — Configuração do Backend

Objetivo:

Criar a fundação do backend.

Tarefas:

* Criar projeto Spring Boot.
* Configurar PostgreSQL.
* Configurar Docker Compose.
* Configurar Flyway.
* Configurar profiles.
* Criar estrutura de pacotes.
* Configurar tratamento global de exceções.
* Configurar validações.
* Criar primeira migration.
* Criar configuração básica da API.

Entregável:

Backend iniciando corretamente e conectando ao PostgreSQL.

---

# SPRINT 03 — Autenticação e segurança

Objetivo:

Criar autenticação para a área administrativa.

Tarefas:

* Criar User.
* Criar Role.
* Implementar Spring Security.
* Implementar autenticação.
* Implementar JWT.
* Criar login.
* Criar proteção das rotas administrativas.
* Implementar autorização por role.
* Criar testes de autenticação.

Entregável:

Usuário ADMIN consegue fazer login e acessar endpoints protegidos.

---

# SPRINT 04 — Categorias e produtos

Objetivo:

Implementar o núcleo do catálogo.

Tarefas:

* Criar Category.
* Criar Product.
* Criar repositories.
* Criar services.
* Criar DTOs.
* Criar controllers.
* Implementar CRUD.
* Implementar ativação/desativação.
* Implementar validações.
* Implementar tratamento de erros.
* Criar testes unitários.
* Criar testes de integração.

Entregável:

Administrador consegue gerenciar produtos e categorias através da API.

---

# SPRINT 05 — Variações e estoque

Objetivo:

Implementar corretamente o domínio específico de uma loja de roupas.

Tarefas:

* Criar Size.
* Criar Color.
* Criar ProductVariant.
* Relacionar variações aos produtos.
* Implementar estoque por variação.
* Implementar atualização de estoque.
* Implementar validações de estoque.
* Impedir estoque negativo.
* Criar consultas de disponibilidade.
* Criar testes para regras de estoque.
* Criar testes de concorrência quando necessário.

Entregável:

Um produto poderá possuir múltiplos tamanhos, cores e estoques independentes.

---

# SPRINT 06 — Imagens

Objetivo:

Permitir que os produtos possuam imagens.

Tarefas:

* Criar ProductImage.
* Criar abstração para armazenamento.
* Implementar upload.
* Associar imagens aos produtos.
* Implementar ordenação.
* Implementar exclusão.
* Validar formatos.
* Validar tamanho dos arquivos.

Inicialmente pode ser utilizado armazenamento local para desenvolvimento, desde que a arquitetura permita substituição posterior por armazenamento externo.

Entregável:

Administrador consegue adicionar e remover fotos dos produtos.

---

# SPRINT 07 — Frontend público

Objetivo:

Criar a experiência do cliente.

Tarefas:

* Configurar React + TypeScript.
* Configurar React Router.
* Criar layout.
* Criar Home.
* Criar catálogo.
* Criar cards de produtos.
* Criar filtros.
* Criar página de detalhes.
* Integrar API.
* Criar estados de loading.
* Criar estados de erro.
* Criar estados de produto indisponível.
* Criar layout responsivo.

Entregável:

Cliente consegue navegar pelo catálogo e visualizar os produtos.

---

# SPRINT 08 — Compra via WhatsApp

Objetivo:

Permitir que o cliente inicie a compra.

Tarefas:

* Seleção de tamanho.
* Seleção de cor.
* Seleção de quantidade.
* Validação de disponibilidade.
* Geração da mensagem.
* Geração do link do WhatsApp.
* Botão de compra.
* Tratamento de produto indisponível.

Entregável:

Cliente consegue selecionar uma peça e iniciar uma conversa no WhatsApp com os dados do produto.

---

# SPRINT 09 — Frontend administrativo

Objetivo:

Criar a interface para gerenciamento da loja.

Tarefas:

* Criar tela de login.
* Criar proteção de rotas.
* Criar dashboard.
* Criar listagem de produtos.
* Criar formulário de produto.
* Criar gerenciamento de categorias.
* Criar gerenciamento de variações.
* Criar gerenciamento de estoque.
* Criar gerenciamento de imagens.
* Criar feedbacks de sucesso/erro.

Entregável:

A proprietária consegue administrar a loja através da interface web.

---

# SPRINT 10 — Qualidade e documentação

Objetivo:

Transformar o MVP em um projeto apresentável para portfólio.

Tarefas:

* Revisar arquitetura.
* Revisar código.
* Melhorar tratamento de erros.
* Revisar validações.
* Adicionar testes faltantes.
* Configurar Testcontainers.
* Configurar Swagger/OpenAPI.
* Melhorar README.
* Criar documentação da arquitetura.
* Criar documentação das decisões técnicas.
* Criar Docker Compose completo.
* Configurar variáveis de ambiente.
* Revisar segurança.
* Revisar CORS.
* Revisar autenticação.
* Revisar gerenciamento de secrets.

Entregável:

MVP completo, documentado e reproduzível.

---

# 17. Roadmap futuro

Não implementar essas funcionalidades no MVP.

Elas fazem parte do roadmap de evolução.

## V2 — Gestão comercial

Adicionar:

* Pedidos.
* Clientes.
* Histórico de pedidos.
* Status do pedido.
* Histórico de estoque.
* Dashboard mais completo.
* Relatórios.
* Auditoria.
* Melhor gerenciamento de variações.

Fluxo:

Cliente
→ Produto
→ Interesse
→ WhatsApp
→ Pedido registrado
→ Status do pedido

---

# V3 — Infraestrutura e produção

Adicionar:

* Docker mais completo.
* CI/CD.
* Deploy.
* HTTPS.
* Cloud storage para imagens.
* Banco PostgreSQL gerenciado.
* Logs estruturados.
* Monitoramento.
* Health checks.
* Métricas.
* Cache quando houver necessidade real.
* Gestão adequada de secrets.
* Melhorias de performance.
* Testes automatizados no pipeline.

---

# V4 — E-commerce completo

Somente quando as versões anteriores estiverem estáveis.

Adicionar:

* Carrinho.
* Checkout.
* Gateway de pagamento.
* Webhooks.
* Cupons.
* Frete.
* Endereço de entrega.
* E-mail transacional.
* Confirmação de pedido.
* Integrações externas.

---

# 18. Regras de desenvolvimento

Durante todo o desenvolvimento:

1. Não implementar todas as sprints de uma vez.
2. Trabalhar uma sprint por vez.
3. Antes de implementar uma funcionalidade, explicar brevemente o objetivo e as decisões técnicas.
4. Evitar overengineering.
5. Não adicionar bibliotecas sem necessidade.
6. Manter separação clara entre Controller, Service e Repository.
7. Não colocar regras de negócio nos Controllers.
8. Não expor entidades JPA diretamente pela API.
9. Utilizar DTOs.
10. Validar dados recebidos pela API.
11. Criar migrations com Flyway.
12. Escrever testes para regras de negócio relevantes.
13. Priorizar código legível em vez de abstrações excessivas.
14. Manter commits pequenos e relacionados a uma única mudança.
15. Atualizar documentação conforme o projeto evolui.
16. Toda nova funcionalidade deve considerar impacto no domínio, banco, API, testes e frontend.
17. Não antecipar funcionalidades das versões futuras sem necessidade.
18. Sempre considerar segurança nas funcionalidades administrativas.
19. Não armazenar secrets diretamente no código ou Git.
20. Quando existir mais de uma solução válida, apresentar os trade-offs antes de escolher.

---

# 19. Objetivo técnico do projeto

O projeto deve demonstrar domínio prático de:

* Java
* Spring Boot
* Spring Security
* REST API
* JPA/Hibernate
* PostgreSQL
* Modelagem relacional
* Migrations
* JWT
* Validação
* Tratamento de exceções
* Testes unitários
* Testes de integração
* Testcontainers
* React
* TypeScript
* Integração frontend/backend
* Docker
* Git
* Documentação de API
* Arquitetura de software
* Modelagem de domínio
* Regras de negócio

O projeto não deve ser tratado apenas como um CRUD.

O foco principal deve ser demonstrar capacidade de construir uma aplicação completa, considerando **domínio, arquitetura, segurança, persistência, regras de negócio, testes, frontend e evolução do sistema**.

---

# 20. Critério de conclusão do MVP

O MVP será considerado concluído quando:

* Administrador conseguir fazer login.
* Administrador conseguir cadastrar categorias.
* Administrador conseguir cadastrar produtos.
* Administrador conseguir cadastrar imagens.
* Administrador conseguir cadastrar tamanhos e cores.
* Administrador conseguir criar variações.
* Administrador conseguir controlar estoque por variação.
* Cliente conseguir visualizar o catálogo.
* Cliente conseguir filtrar produtos.
* Cliente conseguir visualizar detalhes.
* Cliente conseguir selecionar uma variação.
* Cliente conseguir iniciar compra via WhatsApp.
* API estiver documentada.
* Banco estiver versionado via Flyway.
* Testes das principais regras de negócio estiverem implementados.
* Projeto puder ser executado através de Docker Compose.
* README explicar como executar e utilizar o projeto.

Depois disso, o projeto deverá ser considerado **V1/MVP** e somente então deverá avançar para V2.


#Contexto Atual do Projeto

# Contexto do Projeto — Use Autentica (Loja de Roupas)

Você está dando continuidade a um projeto já em andamento. Leia este documento
inteiro antes de responder qualquer coisa. Ele resume tudo que já foi
decidido e implementado até agora, para você não repetir perguntas já
respondidas nem contradizer decisões já tomadas.

---

## 1. O que é o projeto

Aplicação web para uma loja de roupas real chamada **Use Autentica**,
funcionando inicialmente como vitrine digital com gerenciamento
administrativo de produtos e estoque. Repositório: `use-autentica-store`
(contém `backend/` e `frontend/`).

Dois objetivos:
1. Resolver uma necessidade real da loja.
2. Servir como projeto de estudo e portfólio (Java/Spring, React,
   PostgreSQL) — não deve ser tratado como CRUD simples.

MVP sem gateway de pagamento, frete ou checkout completo. O cliente navega
pelo catálogo e inicia a compra via WhatsApp. Área administrativa autenticada
gerencia produtos, categorias, variações (tamanho/cor) e estoque.

O desenvolvimento segue **sprints, uma de cada vez**, com o objetivo e as
decisões técnicas explicados brevemente antes de implementar qualquer coisa
nova. Evitar overengineering e bibliotecas desnecessárias.

Há um documento de especificação completo do projeto (fornecido pelo dono
original) com todas as entidades, regras de negócio, endpoints REST
esperados, sprints 01–10 e roadmap V2/V3/V4. Se você não tem esse documento
em mãos, peça para o usuário reenviá-lo — ele é a fonte da verdade para
regras de negócio e escopo, e este resumo não o substitui integralmente.

---

## 2. Stack e versões confirmadas

- **Backend:** Java 21, **Spring Boot 4.1.1** (confirmado via busca — release
  de agosto/2026, baseado em Spring Framework 7, requer Java 17+).
- Atenção: no Spring Boot 4, `spring-boot-starter-web` foi renomeado para
  **`spring-boot-starter-webmvc`** (o nome antigo ainda funciona como alias
  deprecated). Isso NÃO foi validado com build real — se der erro de
  dependência, é o primeiro lugar a olhar.
- **Frontend:** React + TypeScript + React Router (ainda não iniciado —
  Sprint 07).
- **Banco:** PostgreSQL 16 (via Docker), Flyway para migrations.
- **Sem Lombok** (decisão explícita — tudo escrito à mão, para fins
  didáticos de portfólio).
- **Com Spring Boot DevTools** (hot reload em dev).
- **Sem Spring Boot Actuator por enquanto** (health check manual e simples
  em `/api/health`, decisão explícita para não adicionar dependência sem
  necessidade real ainda).
- **Sem MapStruct** — mapeamento entity↔DTO será manual (decisão adiada
  para Sprint 04, mas provável manter manual pelo motivo didático).

## 3. Convenções de nomenclatura confirmadas

- Group ID: `br.com.useautentica`
- Artifact ID / nome do módulo: `backend`
- Pacote raiz Java: `br.com.useautentica.backend`
- Classe principal: `BackendApplication.java`
- Nome do repositório: `use-autentica-store`
- Nome da loja: **Use Autentica**

---

## 4. Modelagem de domínio — decisões fechadas

| Ponto | Decisão |
|---|---|
| Size / Color | Entidades próprias (tabelas), **não** enums — permite cadastrar novos tamanhos/cores pela área admin sem deploy |
| SKU | Gerado automaticamente pelo sistema (regra de geração ainda não definida em detalhe — ficou para a Sprint 05) |
| Exclusão de ProductVariant | **Soft delete** apenas (`active = false`), nunca hard delete — preserva histórico |
| Categoria por produto | Uma única categoria por produto (relação N:1, não N:N) |
| Preço | Único por produto, **não** varia por variação (tamanho/cor) |

### Modelo de entidades

```
User (id, name, email, password, role, createdAt, updatedAt)
Category (id, name, description, active)
Product (id, name, description, price, active, category [N:1], createdAt, updatedAt)
ProductImage (id, product [N:1], url, altText, displayOrder)
Size (id, name, displayOrder, active)
Color (id, name, hexCode, active)
ProductVariant (id, product [N:1], size [N:1], color [N:1], sku, stockQuantity, active)
  — constraint: unique(product, size, color)
```

---

## 5. O que já foi entregue

### Sprint 01 — Planejamento e modelagem ✅ Concluída

Entregáveis gerados (arquivos `.md` na raiz do repo):
- `README.md` — objetivo, funcionalidades, arquitetura, decisões técnicas,
  roadmap, tabela de status das sprints.
- `diagrama-er.md` — diagrama ER em Mermaid (7 entidades, ver seção 4).
- `requisitos.md` — RFs (RF01–RF19) e RNFs (RNF01–RNF13) numerados.
- `estrutura-projeto.md` — árvore de pastas backend/frontend.

### Sprint 02 — Configuração do backend ✅ Concluída e validada (2026-09-10)

Projeto Spring Boot gerado com a seguinte estrutura em `backend/`:

```
backend/
├── pom.xml                          # Spring Boot 4.1.1, sem Lombok/Actuator, com DevTools
├── .gitignore / .dockerignore
├── .env.example
├── docker-compose.yml               # PostgreSQL 16 + backend
├── Dockerfile                       # multi-stage (Maven build → JRE run)
├── src/main/java/br/com/useautentica/backend/
│   ├── BackendApplication.java
│   ├── config/CorsConfig.java       # libera localhost:5173 (Vite) e :3000
│   ├── controller/publicapi/HealthController.java   → GET /api/health
│   ├── controller/admin/            (vazia, Sprint 03+)
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java   (@RestControllerAdvice)
│   │   ├── ErrorResponse.java            (record — formato padronizado)
│   │   ├── ResourceNotFoundException.java
│   │   ├── BusinessException.java
│   │   └── InvalidStockOperationException.java  (não usada ainda, prep. p/ Sprint 05)
│   └── dto/ entity/ repository/ service/ security/ mapper/   (vazias, com .gitkeep)
├── src/main/resources/
│   ├── application.yml       # ddl-auto: validate (schema 100% via Flyway)
│   ├── application-dev.yml   # show-sql true, log DEBUG
│   ├── application-prod.yml  # placeholder, detalhar na V3
│   └── db/migration/V1__create_users.sql
└── src/test/java/.../BackendApplicationTests.java   # smoke test (@SpringBootTest)
```

**Status real:** validado via `docker compose up --build` em 2026-09-10.
`spring-boot-starter-webmvc` funcionou sem precisar trocar para o nome
antigo. `/api/health` responde 200 e a migration `V1__create_users.sql`
é aplicada com sucesso.

Bug encontrado e corrigido nessa validação: no Spring Boot 4 o autoconfigure
do Flyway foi extraído para o starter `spring-boot-starter-flyway` — com
apenas `flyway-core` no `pom.xml` (como estava originalmente), nenhuma
migration rodava e nenhuma tabela era criada, sem nenhum erro visível nos
logs. Fix aplicado em `backend/pom.xml`: usar `spring-boot-starter-flyway`
+ manter `flyway-database-postgresql` explícito (o starter não traz suporte
a Postgres por padrão). Commitado separadamente (`fix(backend): ...`).

### Sprint 03 — Autenticação e segurança ✅ Concluída e validada (2026-09-10)

Adicionado em `backend/`:
- `entity/User.java`, `entity/Role.java` (enum, só `ADMIN` por enquanto).
- `repository/UserRepository.java`.
- `security/JwtService.java` (geração/validação JWT via `jjwt`),
  `security/JwtAuthenticationFilter.java`, `security/UserDetailsServiceImpl.java`,
  `security/SecurityConfig.java` (`SecurityFilterChain` stateless, `/api/admin/**`
  exige `ROLE_ADMIN`, resto `permitAll`).
- `dto/auth/LoginRequest.java`, `dto/auth/LoginResponse.java`.
- `service/AuthService.java`, `controller/publicapi/AuthController.java`
  → `POST /api/auth/login`.
- `config/AdminUserSeeder.java` — cria o usuário ADMIN inicial no startup a
  partir de `ADMIN_EMAIL`/`ADMIN_PASSWORD` (só se ainda não existir).
- Testes: `security/JwtServiceTest.java` (unitário) e
  `controller/publicapi/AuthControllerTest.java` (login ok/senha errada/rota
  admin sem token), usando profile `test` próprio
  (`src/test/resources/application-test.yml`) pra não depender de env vars
  externas.

**Decisões tomadas:**
- Biblioteca JWT: `jjwt` (não `spring-security-oauth2-resource-server`) —
  emitimos e validamos nosso próprio token, não tem Authorization Server
  externo envolvido.
- Sem refresh token no MVP: só access token de ~24h.
- `Role` como enum, não entidade — já confirmado pela migration `V1`
  (`role VARCHAR(30)`).

**Armadilhas do Spring Boot 4 encontradas nesta sprint** (útil se aparecer
algo parecido de novo):
- `@AutoConfigureMockMvc` mudou de pacote para
  `org.springframework.boot.webmvc.test.autoconfigure` e passou a exigir a
  dependência `spring-boot-webmvc-test` (não vem mais em
  `spring-boot-starter-test`).
- O bean de `ObjectMapper` autoconfigurado pelo Jackson não é mais garantido
  — Boot 4 expõe `jacksonJsonMapper`/`jsonMapperBuilder`. Em `SecurityConfig`
  (usado fora do fluxo normal do Spring MVC, no `authenticationEntryPoint`/
  `accessDeniedHandler`) foi necessário instanciar um `ObjectMapper` próprio
  em vez de injetar, e registrar `findAndRegisterModules()` +
  `disable(WRITE_DATES_AS_TIMESTAMPS)` manualmente pra manter o formato de
  data e os módulos (ex: `java.time`) consistentes com o resto da API.
- `target/test-classes` pode reter arquivos de resource órfãos entre builds
  (ex: um `application.yml` de teste renomeado); isso já causou um bug real
  (datasource vazio) que só um `mvn clean test` resolveu. Se um teste falhar
  de um jeito que não bate com o código atual, vale rodar `clean` antes de
  investigar mais fundo.
- `GlobalExceptionHandler` tinha um `@ExceptionHandler(Exception.class)`
  genérico que interceptava `NoResourceFoundException` (rota não mapeada) e
  devolvia 500 em vez do 404 esperado — corrigido com um handler específico
  para essa exceção. Vale lembrar disso ao adicionar novos catch-alls.

### Sprint 04 — Categorias e produtos ✅ Concluída e validada (2026-09-10)

Adicionado em `backend/`:
- `entity/Category.java` (id, name único, description, active — sem
  timestamps, conforme seção 5 do doc original) e `entity/Product.java`
  (id, name, description, price, active, category [N:1], createdAt,
  updatedAt).
- Migrations `V2__create_categories.sql`, `V3__create_products.sql`
  (`products.category_id` com FK + índice).
- `repository/CategoryRepository.java`, `repository/ProductRepository.java`.
- `dto/category/CategoryRequest.java`/`CategoryResponse.java`,
  `dto/product/ProductRequest.java`/`ProductResponse.java`/
  `ProductSummaryResponse.java`, `dto/ActiveStatusRequest.java`
  (compartilhado entre os PATCH `.../active` de categoria e produto).
- `service/CategoryService.java`, `service/ProductService.java`.
- Controllers públicos (`controller/publicapi/CategoryController.java` →
  `GET /api/categories`; `controller/publicapi/ProductController.java` →
  `GET /api/products`, `GET /api/products/{id}`) e administrativos
  (`controller/admin/CategoryAdminController.java`,
  `controller/admin/ProductAdminController.java`) cobrindo
  POST/PUT/PATCH `active` para categoria e POST/PUT/DELETE/PATCH `active`
  para produto, todos sob `/api/admin/**` (protegido por `ROLE_ADMIN`, já
  coberto pelo `SecurityConfig` da Sprint 03).
- Testes: `service/CategoryServiceTest.java`, `service/ProductServiceTest.java`
  (unitários, Mockito) e testes de integração via `MockMvc` para os 4
  controllers novos (`CategoryAdminControllerTest`, `ProductAdminControllerTest`,
  `CategoryControllerTest`, `ProductControllerTest`). 26 testes passando
  no total (incluindo os da Sprint 03).

**Decisões tomadas (trade-offs apresentados e escolhidos nesta sessão):**
- Categoria inativa **bloqueia** associação a produto — `ProductService`
  chama `CategoryService.findActiveByIdOrThrow`, que lança
  `BusinessException("CATEGORY_INACTIVE", ...)` (HTTP 422) se a categoria
  não estiver ativa. Vale tanto para criar quanto para editar produto.
- `DELETE /api/admin/products/{id}` implementado como **hard delete**
  real já nesta sprint, porque `Product` ainda não tem nenhuma FK
  apontando para ele (variantes/imagens só chegam nas Sprints 05/06).
  Reavaliar a estratégia de exclusão quando essas relações existirem.
- `GET /api/categories` (público) retorna **todas** as categorias, ativas
  e inativas — não existe endpoint de listagem separado para admin na
  seção 9 do doc original, então filtrar por `active` aqui deixaria o
  admin sem forma de ver/reativar categorias inativas pela API.
- Nome de categoria é único (constraint no banco + checagem no service,
  inclusive na edição, usando `existsByNameAndIdNot` para não conflitar
  consigo mesma).
- Sem `ResponseEntity` nos controllers novos — mesmo padrão do
  `AuthController` da Sprint 03 (`@ResponseStatus` fixo por endpoint:
  `201` no create, `204` no delete, `200` implícito no resto).
  `ResponseEntity` fica reservado para quando o status precisa ser
  decidido em runtime, como no `GlobalExceptionHandler`.

**Validação real:** rodado via container Maven (`maven:3.9-eclipse-temurin-21`)
conectado à rede do `docker compose` (não há Maven wrapper nem Maven
instalado localmente na máquina) contra o Postgres do compose — 26 testes,
`BUILD SUCCESS`. Build de produção (`docker compose build backend`)
também validado, e a aplicação subiu com `/api/health` OK e
`/api/categories`, `/api/products` respondendo `[]` após limpeza dos
dados de teste que ficaram no banco de dev (ainda não há Testcontainers
isolando os testes — isso é Sprint 10).

### Sprint 05 — Variações e estoque ✅ Concluída e validada (2026-09-10)

Adicionado em `backend/`:
- `entity/Size.java` (id, name único max 20, displayOrder, active — sem
  timestamps), `entity/Color.java` (id, name único max 50, hexCode
  `#RRGGBB`, active), `entity/ProductVariant.java` (id, product N:1,
  size N:1, color N:1, sku único, stockQuantity, active — sem
  timestamps). Constraint `unique(product_id, size_id, color_id)` e
  `CHECK (stock_quantity >= 0)` no banco.
- Migrations `V4__create_sizes.sql`, `V5__create_colors.sql`,
  `V6__create_product_variants.sql`.
- `repository/SizeRepository.java`, `repository/ColorRepository.java`,
  `repository/ProductVariantRepository.java`.
- `dto/size/*`, `dto/color/*`, `dto/productvariant/*`
  (`ProductVariantRequest`, `ProductVariantResponse` completo p/ admin,
  `ProductVariantSummary` enxuto p/ catálogo público),
  `dto/inventory/StockUpdateRequest.java`.
- `service/SizeService.java`, `service/ColorService.java` (mesmo padrão
  do `CategoryService`: nome único, `findActiveByIdOrThrow`),
  `service/ProductVariantService.java` (gera SKU, valida combinação
  tamanho+cor única, soft delete, `updateStock` absoluto).
- Controllers públicos: `GET /api/sizes`, `GET /api/colors`,
  `GET /api/products/{id}/variants` (só variantes ativas de produto
  ativo). Controllers admin: `SizeAdminController`,
  `ColorAdminController` (POST/PUT/PATCH active),
  `ProductVariantAdminController` (`/api/admin/products/{id}/variants`,
  POST/PUT/DELETE — DELETE é soft delete), `InventoryAdminController`
  (`GET/PATCH /api/admin/inventory`).
- `ProductService.findByIdOrThrow` teve a visibilidade alterada para
  `public` (era `private`) para ser reaproveitado por
  `ProductVariantService`, mesmo padrão já usado com
  `CategoryService.findActiveByIdOrThrow`.
- Testes: `SizeServiceTest`, `ColorServiceTest`, `ProductVariantServiceTest`
  (unitários) e testes de integração para os 6 controllers novos, mais
  extensão do `ProductControllerTest` para o endpoint de variantes.
  56 testes passando no total.

**Decisões tomadas (trade-offs apresentados e escolhidos nesta sessão):**
- **SKU determinístico**: `{6 primeiros chars do productId sem hífen, maiúsculo}-{size.name}-{color.name}`
  (ex: `A3F2C1-M-PRETO`). Garante unicidade sozinho, sem contador extra.
  A coluna `sku` foi dimensionada como `VARCHAR(100)` (não 50) porque o
  pior caso (`size` até 20 chars + `color` até 50 chars) passava de 50 —
  bug real encontrado e corrigido durante a validação com dados de teste
  no limite de tamanho.
- **Update de estoque via `PATCH /api/admin/inventory/{variantId}`
  define valor absoluto** (`{stockQuantity: N}`), não delta — suficiente
  porque no MVP o estoque só muda por ação manual do admin (sem
  carrinho/checkout automático descontando estoque).
- **Tamanho/cor inativos bloqueiam criação de variação**, mesmo padrão
  já usado para categoria inativa bloqueando produto (Sprint 04):
  `ProductVariantService` chama `SizeService.findActiveByIdOrThrow` e
  `ColorService.findActiveByIdOrThrow`.
- **Exclusão de variação é soft delete real** (`active = false`, nunca
  `DELETE` no banco) — já era uma decisão fechada no doc original desta
  sprint. Sem endpoint dedicado de reativação por enquanto (fora do
  escopo explícito da sprint); reativar/trocar tamanho-cor continua
  possível via `PUT`.
- `GET /api/sizes` e `GET /api/colors` (público) não estão na lista de
  endpoints da seção 9 do doc original, mas foram criados espelhando o
  padrão do `CategoryController`, já que "Criar Size"/"Criar Color" são
  tarefas explícitas da sprint e a modelagem como entidade (não enum)
  só faz sentido se houver como cadastrá-las pela API.
- `ProductResponse` (Sprint 04) **não** foi alterado para embutir
  variantes — em vez disso, endpoint próprio
  `GET /api/products/{id}/variants` (só produto ativo, só variantes
  ativas), para não mexer no contrato já existente e não antecipar a
  integração de frontend da Sprint 07.

**Validação real:** mesmo processo das sprints anteriores (container
Maven na rede do `docker compose`, sem Maven local na máquina) — 56
testes, `BUILD SUCCESS`. Durante a validação, um teste com nomes de
tamanho/cor no limite máximo revelou que o SKU podia estourar
`VARCHAR(50)`; corrigido para `VARCHAR(100)` na migration e na entidade,
com reset do volume Postgres de dev (só continha dado de teste) para o
Flyway reaplicar as 6 migrations do zero. Build de produção
(`docker compose build backend`) também validado, aplicação subiu e
`/api/health`, `/api/sizes`, `/api/colors`, `/api/products` responderam
corretamente antes da limpeza final dos dados de teste.

### Sprint 06 — Imagens ✅ Concluída e validada (2026-09-10)

Adicionado em `backend/`:
- `entity/ProductImage.java` (id, product N:1, storageKey, url, altText,
  displayOrder — sem `active`/soft delete, exclusão é hard delete real
  do registro e do arquivo).
- Migration `V7__create_product_images.sql`.
- `repository/ProductImageRepository.java`.
- `storage/ImageStorageService.java` (interface: `store(MultipartFile)` /
  `delete(storageKey)`) + `storage/LocalImageStorageService.java`
  (implementação em disco local, caminho configurável via
  `app.storage.local.base-path` / env `STORAGE_LOCAL_BASE_PATH`).
- `dto/productimage/ProductImageResponse.java`.
- `service/ProductImageService.java` (valida formato — jpeg/png/webp —,
  rejeita arquivo vazio, calcula `displayOrder` automático pela contagem
  de imagens do produto, apaga arquivo + registro no delete).
- `controller/admin/ProductImageAdminController.java` →
  `POST /api/admin/products/{id}/images` (multipart, campo `file` +
  `altText` opcional) e `DELETE /api/admin/products/{id}/images/{imageId}`.
- `config/WebConfig.java` — expõe `/uploads/**` como resource handler
  apontando pro `base-path` local (já cai no `permitAll` do
  `SecurityConfig`, que só protege `/api/admin/**`).
- `GlobalExceptionHandler` ganhou handler pra
  `MaxUploadSizeExceededException` → 413 `IMAGE_TOO_LARGE`.
- `ProductResponse` (Sprint 04) passou a incluir `List<ProductImageResponse> images`;
  `ProductService` ganhou dependência de `ProductImageRepository` pra
  popular essa lista.
- `application.yml`: `spring.servlet.multipart.max-file-size`/`max-request-size`
  em `5MB`, `app.storage.local.base-path`.
- `docker-compose.yml`: volume novo `use-autentica-uploads-data` montado
  em `/app/uploads` no serviço `backend`, pra persistir uploads entre
  reinícios do container (mesmo motivo do volume do Postgres).
- Testes: `ProductImageServiceTest` (unitário) e
  `ProductImageAdminControllerTest` (integração, incluindo upload real
  via `MockMultipartFile` e verificação de que a imagem aparece embutida
  no `GET /api/products/{id}` público). 66 testes passando no total.

**Decisões tomadas (trade-offs apresentados e escolhidos nesta sessão):**
- **Imagens embutidas em `ProductResponse`** (não endpoint próprio como
  fizemos pra variações na Sprint 05) — a seção 7 do doc original lista
  "Imagens" junto com nome/preço/etc no mesmo detalhe do produto, e não
  existe um GET público dedicado pra imagens na seção 9.
- **Upload real via multipart** (`file`), não só uma URL de referência —
  as tarefas da sprint pedem explicitamente "implementar upload",
  "validar formatos" e "validar tamanho dos arquivos", o que só faz
  sentido recebendo o arquivo de fato.
- **Limite de tamanho delegado ao Spring** (`multipart.max-file-size: 5MB`)
  em vez de checagem manual no service — fonte única de verdade; o
  `GlobalExceptionHandler` traduz o erro do Spring pra um
  `ErrorResponse` no mesmo formato do resto da API.
- **Exclusão de imagem é hard delete real** (apaga registro e arquivo em
  disco) — diferente da decisão de soft delete pra `ProductVariant`
  (Sprint 05); aqui não há necessidade de preservar histórico de imagem
  removida.
- **Sem endpoint de reordenação** — a seção 9 só lista POST e DELETE
  pra imagens; ordenação é resolvida com `displayOrder` automático
  (posição de chegada) no upload.

**Validação real:** mesmo processo das sprints anteriores — 66 testes via
container Maven, `BUILD SUCCESS`. Build de produção
(`docker compose build backend`) validado, e smoke test end-to-end real
com `curl` (login → criar categoria/produto → upload de um JPEG via
multipart → `GET /api/products/{id}` retornando a imagem embutida →
`GET /uploads/{arquivo}` servindo o arquivo com `Content-Type: image/jpeg`
correto → `DELETE` da imagem confirmando remoção do arquivo do disco,
`/uploads/{arquivo}` passou a responder 404). Dados de teste limpos do
banco e do volume de uploads ao final.

---

### Sprint 07 — Frontend público ✅ Concluída e validada (2026-09-11)

Criado o módulo `frontend/` (Vite + React 19 + TypeScript, antes vazio):

```
frontend/
├── index.html                 # título "Use Autêntica", Google Fonts (Cormorant Garamond + Montserrat)
├── .env                        # VITE_API_BASE_URL=http://localhost:8080 (sem segredo, commitado)
├── .oxlintrc.json              # linter padrão do template Vite atual (oxlint, não ESLint)
├── src/
│   ├── main.tsx / App.tsx      # BrowserRouter + rotas (/, /catalogo, /produtos/:id, 404)
│   ├── pages/                  # Home, Catalog, ProductDetail, NotFound (+ .module.css cada)
│   ├── components/
│   │   ├── layout/              # Header, Footer, Layout (Outlet)
│   │   ├── product/              # ProductCard, ImageGallery, VariantList
│   │   └── common/                # LoadingIndicator, ErrorMessage
│   ├── api/                    # client.ts (fetch wrapper + ApiError), products/categories/sizes/colors.ts
│   ├── types/                  # product, category, size, color, variant
│   ├── hooks/useAsync.ts       # hook genérico de fetch com loading/error
│   ├── utils/currency.ts       # formatPrice (Intl BRL)
│   └── styles/variables.css    # paleta e tipografia da identidade visual (seção 9)
```

**Decisões técnicas (trade-offs apresentados e escolhidos nesta sessão):**
- **Vite** (não Create React App, descontinuado) — HMR rápido, é o padrão atual;
  o `CorsConfig` do backend já liberava `localhost:5173`.
- **CSS Modules puro**, sem Tailwind — alinhado à stack original ("HTML, CSS"),
  zero dependência nova.
- **Fetch API nativa**, sem Axios — suficiente para os poucos endpoints públicos
  do MVP, zero dependência nova.
- **Google Fonts via `<link>`** no `index.html`, não `@fontsource` — zero
  dependência npm.
- Template do Vite atual gera **oxlint** por padrão (não mais ESLint/
  `eslint.config.js`) — mantido como está por ser a escolha própria do time
  do Vite e não exigir dependências extras; extensão do VS Code trocada de
  ESLint para `oxc.oxc-vscode` para bater com a ferramenta real do projeto.
- Cliente HTTP (`api/client.ts`) traduz o formato de erro padronizado do
  backend (`ErrorResponse`) numa classe `ApiError` própria no frontend.

**Lacuna de API descoberta durante a integração (resolvida nesta sessão,
mexendo no backend fora do escopo original da sprint):** `GET /api/products`
(`ProductSummaryResponse`) só trazia `id, name, price, active, categoryName`
— sem imagem, disponibilidade de estoque ou tamanho/cor, o que impedia 3
tarefas da sprint (card com foto, estado de indisponível na listagem, filtro
por tamanho/cor). Corrigido no backend:
- `ProductSummaryResponse` ganhou `coverImageUrl` (primeira imagem por
  `displayOrder`, ou `null`) e `available` (true se existir ao menos uma
  variação ativa com estoque > 0), calculados em lote (uma query extra para
  todos os produtos da página, não uma por produto) em
  `ProductService.findActiveForCatalog`.
- `GET /api/products` passou a aceitar `categoryId`, `sizeId`, `colorId`
  como query params opcionais, via nova query JPQL em `ProductRepository`
  (`EXISTS` subquery contra `ProductVariant` para tamanho/cor, independentes
  entre si — não exige que seja a mesma variação).
- Filtro por categoria e ordenação por preço no Catálogo do frontend usam
  esses parâmetros/campo já existentes; a ordenação em si continua sendo
  feita no cliente (não precisa de suporte do backend).

**Bug real encontrado e corrigido nesta sessão (não relacionado à lacuna
acima):** `DELETE /api/admin/products/{id}` retornava 500 genérico quando o
produto tinha qualquer variação (mesmo soft-deleted), porque a FK de
`product_variants` bloqueava o hard delete — risco que já estava anotado na
Sprint 04 ("reavaliar quando essas relações existirem"). Corrigido com uma
checagem explícita em `ProductService.delete` (`ProductVariantRepository
.existsByProductId`) lançando `BusinessException("PRODUCT_HAS_VARIANTS", ...)`
→ 422, em vez de deixar a constraint do banco estourar como 500. A estratégia
de exclusão continua sendo hard delete quando não há variação; produtos com
variação devem ser desativados, não excluídos.

**Testes:** 3 novos (2 unitários em `ProductServiceTest`, 1 de integração em
`ProductAdminControllerTest` cobrindo o 422 de `PRODUCT_HAS_VARIANTS`) — 69
testes passando no total, `BUILD SUCCESS` via container Maven.

**Validação real:** backend rebuildado (`docker compose build backend`) e
validado rodando; frontend rodado com `npm run dev` e testado de ponta a
ponta com Playwright (Chromium headless e Edge, desktop 1280px e mobile
390px) contra a API real — Home (categorias + destaques), Catálogo (filtros
de categoria/tamanho/cor + ordenação, card com imagem real e badge
"Indisponível", estado vazio), Detalhe do produto (galeria de imagens,
lista de variações com disponibilidade) — sem erros de console, TypeScript
e `oxlint` limpos. Dados de teste usados na validação foram desativados/
removidos ao final (a limpeza completa do banco de dev — poluído por dados
de teste acumulados de sprints anteriores, não só desta sessão, já que ainda
não há Testcontainers isolando os testes — ficou pendente de aprovação
manual do usuário para uma operação de `TRUNCATE`).

---

### Sprint 08 — Compra via WhatsApp ✅ Concluída e validada (2026-09-11)

Adicionado em `frontend/`:
- `utils/whatsapp.ts` — `buildWhatsAppLink(productName, variant, quantity, price)`
  monta a mensagem (mesmo formato do exemplo da seção 7 do doc original) e
  o link `https://wa.me/{numero}?text=...`. Número lido de
  `VITE_WHATSAPP_NUMBER` (`.env`).
- `components/product/VariantList.tsx` deixou de ser só leitura: cada linha
  virou um `<button>` clicável (seleciona a variação), desabilitado quando
  `available` é `false`, com destaque visual (borda) na variação
  selecionada.
- `components/product/PurchaseWhatsApp.tsx` (+ `.module.css`) — aparece só
  depois de uma variação selecionada: campo de quantidade (`min=1`,
  `max=stockQuantity` da variação, clampado no `onChange`) e botão "Comprar
  pelo WhatsApp" (`<a target="_blank">` com o link montado).
- `pages/ProductDetail.tsx` passou a guardar a variação selecionada em
  estado local; se **nenhuma** variação do produto está disponível, mostra
  "Produto indisponível no momento." em vez do seletor (tratamento de
  produto indisponível pedido pela sprint).

**Decisão técnica (trade-off apresentado e escolhido nesta sessão):**
- **Seleção por variação única** (a linha já combina tamanho+cor+estoque),
  em vez de dois `<select>` separados de tamanho e cor — o doc original
  descreve o fluxo como "cliente seleciona a variação" (uma ação), a
  estrutura de dados já vem pronta da Sprint 05
  (`GET /api/products/{id}/variants`) e evita ter que calcular/desenhar
  combinações tamanho×cor inválidas na tela.

**Número de WhatsApp:** por pedido do dono do projeto, usado o número
pessoal dele **como placeholder de teste** (`VITE_WHATSAPP_NUMBER` no
`.env` do frontend) — troca para o número real da loja é uma tarefa
pendente e explícita para quando o MVP for pra deploy (ver seção 7).

**Testes:** funcionalidade validada manualmente via Playwright (Edge,
desktop e mobile) contra a API real — seleção de variação, ajuste de
quantidade, geração do link `wa.me` com o texto correto (conferido
decodificando a URL), e o estado de "produto indisponível" quando nenhuma
variação tem estoque. Sem testes automatizados novos (é lógica só de
frontend, sem regra de negócio no backend) — `tsc` e `oxlint` limpos.

---

### Sprint 09 — Frontend administrativo ✅ Concluída e validada (2026-09-11)

Adicionado em `frontend/`:
- `auth/session.ts` — guarda a sessão (`token`, `tokenType`, `name`, `email`,
  `role`) em `localStorage`; `auth/AuthContext.tsx` expõe `login`/`logout`/
  `session` via React Context; `admin/ProtectedRoute.tsx` redireciona pra
  `/admin/login` quando não há sessão.
- `api/client.ts` ganhou `apiPost`/`apiPut`/`apiPatch`/`apiDelete`/
  `apiUpload`, todos anexando `Authorization: Bearer` quando `auth: true`
  (padrão pras rotas admin) — em 401 limpa a sessão e redireciona pro
  login automaticamente.
- `api/admin/*.ts` (products, categories, sizes, colors, inventory,
  variants, images) — um módulo por recurso administrativo.
- `admin/AdminLayout.tsx` — sidebar com navegação (Dashboard, Produtos,
  Categorias, Estoque) e botão de logout.
- `admin/pages/Login.tsx`, `Dashboard.tsx`, `Categories.tsx`,
  `ProductsList.tsx`, `ProductForm.tsx`, `Inventory.tsx`.
- `admin/components/VariantManager.tsx` e `ImageManager.tsx` — usados
  dentro do `ProductForm` (só aparecem depois do produto já criado, já
  que variação/imagem exigem um `productId`).

**Lacuna de API descoberta e corrigida nesta sessão (igual ao padrão da
Sprint 07):** não existia nenhum jeito de listar/ver produtos inativos
pela API — só o público `GET /api/products` (só ativos). Sem isso, a tela
"Produtos" do admin não conseguia mostrar nem editar produtos desativados.
Adicionado `GET /api/admin/products` (todos os produtos, reaproveitando o
`ProductSummaryResponse` da Sprint 07) e `GET /api/admin/products/{id}`
(qualquer produto, ativo ou não) em `ProductAdminController` +
`ProductService` (`findAllForAdmin`, `findByIdForAdmin`; a lógica de
enriquecimento com imagem de capa/disponibilidade foi extraída pra um
método privado `toSummaries` compartilhado com o catálogo público).
`GET /api/admin/inventory` (Sprint 05) já listava todas as variações de
todos os produtos, então os números de estoque do dashboard não precisaram
de nenhuma mudança no backend.

**Decisões técnicas (trade-offs apresentados e escolhidos nesta sessão):**
- **Token em `localStorage`**, lido por um Context no boot da aplicação —
  simples, sem refresh token (mesma decisão da Sprint 03: só access token).
- **Formulários com `useState` simples**, sem `react-hook-form`/`zod` — a
  validação de verdade já é o Bean Validation do backend; os formulários
  admin não são complexos o bastante pra justificar a dependência.
- **Sem tela própria de tamanhos/cores** — o doc original não lista essa
  tela na Sprint 09, mas sem cadastrar tamanho/cor não dá pra criar
  variação nenhuma; resolvido cadastrando-os inline dentro do
  `VariantManager` do próprio formulário de produto, sem inventar uma tela
  nova fora do escopo descrito.
- **Confirmação de exclusão via `window.confirm`** (produto e variação) —
  suficiente para o MVP, evita construir um componente de modal só pra
  isso.
- **Estoque "baixo"** definido como `stockQuantity <= 5` (constante no
  frontend, `LOW_STOCK_THRESHOLD`) — o doc pede identificar estoque baixo
  mas não define o limiar; 5 é um valor de partida razoável, fácil de
  ajustar depois se a proprietária achar melhor outro número.

**Testes:** 3 novos no backend (2 unitários em `ProductServiceTest`
cobrindo `findAllForAdmin`/`findByIdForAdmin`, 1 de integração em
`ProductAdminControllerTest`) — 72 testes passando no total, `BUILD
SUCCESS`. Sem testes automatizados novos no frontend (mesma decisão da
Sprint 08).

**Validação real:** backend rebuildado e validado rodando; frontend
testado de ponta a ponta com Playwright (Edge) contra a API real —
redirecionamento de rota protegida sem sessão, login, dashboard com os 4
números, CRUD de categoria, criação de produto com redirecionamento pro
formulário de edição, cadastro de variação, upload de imagem, alteração
de estoque na tela de Estoque (conferida via API depois), logout e
reproteção da rota — sem erros de console, `tsc` e `oxlint` limpos. Dados
de teste (da validação manual e dos testes automatizados do backend, que
ainda rodam contra o banco de dev compartilhado) limpos com `TRUNCATE` ao
final.

---

## 6. Regras de processo que devem continuar sendo seguidas

(vindas do documento de especificação original, seção 18 — reforçar sempre)

1. Uma sprint por vez, nunca implementar tudo de uma vez.
2. Antes de implementar algo novo, explicar brevemente objetivo e decisões
   técnicas.
3. Evitar overengineering e bibliotecas sem necessidade real.
4. Separação clara Controller → Service → Repository; regra de negócio
   nunca no Controller.
5. Nunca expor entidades JPA diretamente pela API — sempre DTOs.
6. Validar dados recebidos (Bean Validation).
7. Migrations sempre via Flyway.
8. Escrever testes para regras de negócio relevantes.
9. Commits pequenos, um assunto por commit.
10. Quando houver mais de uma solução válida, apresentar os trade-offs
    antes de escolher (isso já vem sendo feito ativamente na conversa —
    ex: Lombok, DevTools, Actuator, MapStruct, Size/Color como
    entidade vs enum).

---

## 7. Próximo passo

**Sprint 10 — Qualidade e documentação**, ainda não iniciada (última
sprint do MVP). Objetivo do documento original: revisar arquitetura e
código, melhorar tratamento de erros e validações, adicionar testes
faltantes, configurar Testcontainers, configurar Swagger/OpenAPI, melhorar
o README, documentar arquitetura e decisões técnicas, Docker Compose
completo, revisar segurança/CORS/autenticação/gerenciamento de secrets.
Entregável: "MVP completo, documentado e reproduzível."

Itens que já sabemos que essa sprint precisa cobrir, levantados ao longo
das sessões anteriores:
- **Testcontainers** — vai finalmente isolar os testes do banco de dev
  compartilhado. Hoje toda rodada de `mvn test` deixa categorias/produtos
  de teste no Postgres de dev (ver notas de validação das Sprints 07/08/09);
  isso para de acontecer assim que os testes passarem a subir seu próprio
  Postgres efêmero.
- **Swagger/OpenAPI** — a seção 15 do doc original pede documentação
  completa dos endpoints; ainda não foi feita em nenhuma sprint anterior.
- **Revisar o 500 → 422 do `PRODUCT_HAS_VARIANTS`** (Sprint 07) e outros
  pontos de tratamento de erro — bom momento para uma revisão geral do
  `GlobalExceptionHandler`.
- **README** — hoje só existe o gerado na Sprint 01 (planejamento); precisa
  refletir o estado real do projeto (como rodar backend+frontend juntos,
  variáveis de ambiente de cada um, etc).

Seguir o mesmo padrão desta conversa: explicar objetivo e decisões técnicas,
perguntar preferências quando houver mais de uma opção válida, e só então
gerar código.

Importante: **antes do deploy real do MVP**, trocar `VITE_WHATSAPP_NUMBER`
(hoje o número pessoal do dono do projeto, usado só para teste) pelo
número oficial da loja — ver nota da Sprint 08. Essa troca não depende da
Sprint 10, pode ser feita a qualquer momento antes de publicar o site de
verdade.

## 8. Regras de processo combinadas ao longo das sessões

Além da seção 18 do documento original:

- Commits: usar Conventional Commits, um assunto por commit, nunca agrupar
  mudanças não relacionadas. Não incluir atribuição de IA nos commits deste
  repositório.
- Comentários no código: curtos, só quando o "porquê" não é óbvio. Não
  referenciar número de sprint nem anunciar planos futuros em comentários —
  isso apodrece; esse tipo de contexto fica neste documento, não no código.
- Ao finalizar uma sprint, atualizar este documento (seção 5 e a seção
  "Próximo passo") antes de seguir para a próxima, para ele continuar
  servindo como checklist entre sessões.
- Não escrever código, testes ou tratamento de erro que dependam de como
  vai ficar uma sprint futura ainda não implementada — nem pra "só validar
  algo agora". Se testar algo exige assumir a existência de uma rota/entidade
  que só vai existir depois, é sinal de que está fora de escopo da sprint
  atual (aconteceu na Sprint 03: um teste que assumia a ausência de
  endpoints da Sprint 04 acabou puxando uma correção de exception handler
  que não era o foco da sprint).

---

## 9. Identidade visual da marca (Use Autêntica)

Manual de marca recebido do dono do projeto em 2026-09-10, arquivo original
em `docs/identidade-visual/manual-marca.pdf` (versionado no Git). Usar
sempre como referência de cores e tipografia a partir da Sprint 07
(frontend). O resumo abaixo foi transcrito manualmente a partir do manual —
conferir o PDF original em caso de dúvida de detalhe visual (ex: variações
do logo, texturas de fundo).

### Logo

- Nome da loja: **use autêntica** (minúsculo no logo), tagline "MODA
  FEMININA".
- Monograma: "UA" com um coraçãozinho embutido.
- Versões: principal (monograma + nome + tagline), horizontal (monograma +
  nome ao lado), e versão para fundos escuros (fundo marrom profundo).

### Paleta de cores

| Nome | Hex | Uso |
|---|---|---|
| Marrom Dourado | `#8A5D16` | Logo, destaques, botões principais, elementos de destaque |
| Caramelo Elegante | `#A0783C` | Elementos secundários, ícones, detalhes, hover |
| Bege Nude | `#D8C5A8` | Fundos, áreas suaves, componentes |
| Off-White | `#F7F3EC` | Fundo principal, contraste leve |
| Marrom Profundo | `#3B2A18` | Textos, ícones, rodapés, contraste |

### Tipografia

- Títulos e destaques: **Cormorant Garamond** (serifada).
- Textos e informações: **Montserrat** (sans-serif).
- Ambas são Google Fonts — decidir na Sprint 07 se serão importadas via
  `@fontsource` (pacote npm) ou via link do Google Fonts no `index.html`
  (apresentar esse trade-off antes de escolher, como de costume).

### Estilo / conceito visual

Elegante, feminino, sofisticado e minimalista. Espaço em branco generoso,
tons neutros e quentes, detalhes em marrom dourado. Visual clean, moderno e
atemporal, com foco na experiência de compra e na beleza dos produtos.
Elementos gráficos de apoio: monograma, coraçãozinho, linha fina, moldura
simples; texturas suaves (papel, tecido) em fundos quando fizer sentido.

### Botões e componentes (referência visual, não funcional ainda)

- Botão principal: fundo `#8A5D16`, texto claro.
- Hover do botão principal: tom mais escuro de marrom.
- Botão secundário: outline, sem preenchimento.
- Card de produto: imagem, nome, preço, avaliação em estrelas, botão de
  ação.
- Ícones de referência no manual: busca, usuário, coração (favoritos),
  sacola, menu.

**Atenção de escopo:** o manual de marca mostra ícone de carrinho/sacola e
avaliação por estrelas como exemplo de estilo visual — isso **não** significa
que carrinho, favoritos ou avaliações entram no MVP. Esses recursos não
estão nas Sprints 07/08 nem no MVP (ver seção 7 e seção 17 no topo do
documento — carrinho/checkout são V4). Usar o manual só para cores,
tipografia e estilo dos componentes que já estão no escopo (cards de
produto, botões, layout).
