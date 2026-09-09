# Requisitos — MVP Plataforma Web para Loja de Roupas

## Requisitos Funcionais

### Área pública

- RF01: O sistema deve exibir uma página inicial (Home) com nome/logo da loja, categorias, destaques e link para o catálogo.
- RF02: O sistema deve permitir listar produtos ativos no catálogo.
- RF03: O sistema deve permitir filtrar produtos por categoria, tamanho e cor.
- RF04: O sistema deve permitir ordenar produtos por preço.
- RF05: O sistema deve exibir detalhes de um produto (nome, descrição, preço, imagens, variações disponíveis).
- RF06: O sistema deve identificar visualmente variações sem estoque como indisponíveis.
- RF07: O sistema deve permitir que o cliente selecione tamanho, cor e quantidade de uma variação disponível.
- RF08: O sistema deve gerar uma mensagem pré-formatada e abrir o WhatsApp da loja com os dados do produto selecionado.

### Área administrativa

- RF09: O sistema deve permitir que um usuário ADMIN faça login com email e senha, recebendo um token JWT.
- RF10: O sistema deve proteger todas as rotas administrativas, exigindo autenticação.
- RF11: O sistema deve exibir um dashboard com total de produtos, produtos ativos, produtos sem estoque e quantidade total de itens disponíveis.
- RF12: O sistema deve permitir criar, editar, ativar/desativar e excluir produtos.
- RF13: O sistema deve permitir cadastrar, editar, ativar/desativar categorias.
- RF14: O sistema deve permitir cadastrar e remover imagens de um produto, com controle de ordem de exibição.
- RF15: O sistema deve permitir cadastrar tamanhos e cores.
- RF16: O sistema deve permitir criar variações (tamanho + cor) de um produto, com SKU gerado automaticamente.
- RF17: O sistema deve permitir consultar e alterar o estoque de cada variação individualmente.
- RF18: O sistema deve identificar produtos/variações sem estoque ou com estoque baixo.
- RF19: O sistema deve impedir a exclusão definitiva (hard delete) de uma variação — apenas desativação (soft delete).

## Requisitos Não Funcionais

- RNF01: A API deve seguir o padrão REST, com respostas em JSON.
- RNF02: A autenticação deve ser feita via JWT, sem manter sessão no servidor.
- RNF03: Nenhuma entidade JPA deve ser exposta diretamente pelos controllers — uso obrigatório de DTOs.
- RNF04: Toda entrada de dados via API deve ser validada (Bean Validation).
- RNF05: O estoque nunca pode ficar negativo, em nenhuma operação.
- RNF06: As migrations do banco devem ser versionadas via Flyway e mantidas no Git.
- RNF07: A aplicação deve poder ser executada localmente via Docker Compose.
- RNF08: A API deve ser documentada via OpenAPI/Swagger.
- RNF09: Regras de negócio relevantes (especialmente estoque) devem possuir testes automatizados.
- RNF10: Erros da API devem seguir um formato padronizado de resposta.
- RNF11: Segredos (senhas, chaves JWT, credenciais de banco) não devem ser versionados no Git.
- RNF12: A arquitetura de armazenamento de imagens deve permitir troca futura de provedor sem reescrever regras de negócio.
- RNF13: O sistema não deve implementar checkout, pagamento ou frete nesta versão (fora de escopo do MVP).

## Fora de escopo do MVP (roadmap futuro)

- Carrinho de compras, checkout e gateway de pagamento (V4).
- Registro formal de pedidos e histórico de cliente (V2).
- CI/CD, deploy em produção, cloud storage (V3).
