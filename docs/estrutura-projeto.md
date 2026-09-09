# Estrutura Inicial do Projeto

## Backend (Spring Boot)

Organização por responsabilidade/camada, conforme definido no documento de projeto:

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/loja/roupas/
│   │   │       ├── config/            # configurações gerais (CORS, Swagger, beans)
│   │   │       ├── controller/        # controllers REST
│   │   │       │   ├── admin/
│   │   │       │   └── public/
│   │   │       ├── dto/
│   │   │       │   ├── auth/
│   │   │       │   ├── product/
│   │   │       │   ├── category/
│   │   │       │   ├── variant/
│   │   │       │   ├── image/
│   │   │       │   └── inventory/
│   │   │       ├── entity/            # entidades JPA
│   │   │       ├── repository/        # interfaces Spring Data JPA
│   │   │       ├── service/           # regras de negócio
│   │   │       ├── security/          # JWT, filtros, SecurityConfig
│   │   │       ├── exception/         # exceções customizadas + handler global
│   │   │       ├── mapper/            # entity <-> DTO
│   │   │       └── LojaRoupasApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/          # scripts Flyway (V1__..., V2__...)
│   └── test/
│       └── java/com/loja/roupas/
│           ├── service/               # testes unitários
│           └── integration/           # testes com Testcontainers
├── Dockerfile
└── pom.xml
```

## Frontend (React + TypeScript)

Organização inicial por feature, separando área pública da área administrativa:

```
frontend/
├── src/
│   ├── api/                # cliente HTTP (axios), configuração base
│   ├── components/         # componentes reutilizáveis (Button, Card, Loader...)
│   ├── features/
│   │   ├── catalog/        # Home, catálogo, filtros, detalhes do produto
│   │   ├── whatsapp/       # geração de link/mensagem de compra
│   │   └── admin/
│   │       ├── auth/       # login, contexto de autenticação
│   │       ├── dashboard/
│   │       ├── products/
│   │       ├── categories/
│   │       └── inventory/
│   ├── routes/             # configuração do React Router, rotas protegidas
│   ├── types/              # tipos TypeScript compartilhados
│   ├── hooks/               # hooks customizados
│   ├── App.tsx
│   └── main.tsx
├── public/
├── package.json
└── tsconfig.json
```

## Infraestrutura

```
/
├── backend/
├── frontend/
├── docker-compose.yml       # PostgreSQL + backend (+ frontend futuramente)
├── .env.example
└── README.md
```

### Observações

- A separação `public/` vs `admin/` nos controllers e `catalog/` vs `admin/` no frontend reforça a diferença de autenticação entre as duas áreas desde o início.
- `mapper/` isola a conversão entre entidades e DTOs, evitando lógica de mapeamento espalhada em services e controllers.
- A estrutura pode evoluir para organização por domínio/feature no backend caso o projeto cresça, mas para o tamanho atual do MVP a separação por camada é suficiente e mais simples de navegar.
