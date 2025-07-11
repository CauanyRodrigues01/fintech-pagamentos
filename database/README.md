# 📦 Banco de Dados

Esta pasta contém os scripts SQL utilizados na criação, população e manipulação do banco de dados do sistema de pagamentos.

## Arquivos

- `schema.sql`: script de criação das tabelas `cliente` e `fatura`.
- `seed.sql`: dados iniciais para popular o banco com exemplos.
- `queries.sql`: querie que retorna cliente com mais de 3 dias de atraso e status "Bloqueado" e uma querie que atualiza o limite de crédito para zero desses clientes bloqueados .
- `README.md`: esta explicação sobre a estrutura do banco.
- `Diagrama ER`: imagem do diagrama ER exposto neste arquivo.

## Observações

- O campo `status_bloqueio` do cliente pode ser:
  
  - `A`: Ativo
  - `B`: Bloqueado

- O campo `status` da fatura pode ser:
  
  - `P`: Paga
  - `A`: Atrasada
  - `B`: Aberta

## Execução

Use os arquivos `schema.sql` e `seed.sql` para criar e popular o banco no SGBD PostgreSQL (utilizado no projeto).

## Diagrama ER

![Diagrama ER.png](https://github.com/CauanyRodrigues01/fintech-pagamentos/blob/main/database/Diagrama%20ER.png?raw=true)
