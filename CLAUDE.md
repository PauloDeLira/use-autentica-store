# Use Autentica — instruções para o Claude

Antes de responder qualquer coisa neste repositório, leia
`docs/historico-do-projeto.md` por inteiro. Ele é a fonte de verdade do
projeto: especificação completa, entidades, regras de negócio,
sprints 01–10, decisões técnicas já tomadas e o status atual (o que
foi entregue, o que falta, qual é o próximo passo). Não repita
perguntas já respondidas nem contradiga decisões já tomadas lá.

## Regras de processo (resumo — o detalhe completo está no doc)

- Uma sprint por vez, nunca implementar tudo de uma vez.
- Antes de implementar algo novo, explicar objetivo e decisões técnicas;
  quando houver mais de uma solução válida, apresentar os trade-offs
  antes de escolher.
- Ao finalizar uma sprint, atualizar `docs/historico-do-projeto.md` (seção de
  status e "Próximo passo") antes de seguir para a próxima.
- Commits: Conventional Commits, um assunto por commit, sem referência
  a número de sprint no assunto.
- Comentários no código: curtos, só quando o "porquê" não é óbvio.

## Setup local

- `backend/.env` não está no Git (contém segredos reais). Copie
  `backend/.env.example` para `backend/.env` e ajuste os valores — ou
  copie o `.env` real de outra máquina por um canal seguro (ex:
  gerenciador de senhas), já que são as mesmas credenciais de dev nos
  dois lugares.
- Não há Maven local nem `mvnw` — `mvn` roda via container
  (`maven:3.9-eclipse-temurin-21`) na rede do `docker compose`. Ver
  seção de validação de cada sprint em `docs/historico-do-projeto.md` para o
  comando exato usado.
- Subir a aplicação: `docker compose up --build` dentro de `backend/`.
