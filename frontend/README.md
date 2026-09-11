# Use Autêntica — Frontend

React + TypeScript + Vite. Consome a API do backend (ver `../backend`).

## Rodando localmente

```bash
npm install
npm run dev
```

Abre em `http://localhost:5173`. Requer o backend rodando (ver README na
raiz do repositório).

## Variáveis de ambiente

Definidas em `.env` (já commitado — nenhuma delas é segredo):

| Variável | Descrição |
|---|---|
| `VITE_API_BASE_URL` | URL base da API (`http://localhost:8080` em dev) |
| `VITE_WHATSAPP_NUMBER` | Número de WhatsApp da loja, formato `55DDDNNNNNNNNN` |

## Estrutura

```
src/
├── admin/         # painel administrativo: rotas, layout, páginas, componentes próprios
├── api/            # cliente HTTP (client.ts) + uma função por endpoint, agrupadas por recurso
├── auth/            # sessão do admin (contexto React + localStorage)
├── components/       # componentes públicos reutilizáveis (layout, produto, comuns)
├── hooks/              # useAsync — hook genérico de fetch com loading/error
├── pages/               # páginas públicas: Home, Catalog, ProductDetail, NotFound
├── styles/               # variables.css — tokens de design (cores, tipografia, espaçamento)
├── types/                 # tipos TS espelhando os DTOs do backend
└── utils/                  # formatação de preço, geração de link do WhatsApp
```

## Scripts

- `npm run dev` — servidor de desenvolvimento (Vite + HMR)
- `npm run build` — build de produção (`tsc -b && vite build`)
- `npm run lint` — oxlint
- `npm run preview` — serve o build de produção localmente

## Decisões técnicas

Sem Axios (Fetch API nativa é suficiente para os endpoints do MVP), sem
Tailwind (CSS Modules puro, seguindo a paleta em `styles/variables.css`),
sem React Query/Redux (estado assíncrono simples via `useAsync`). Detalhes
e trade-offs completos em `../docs/promptprojeto.md`.
