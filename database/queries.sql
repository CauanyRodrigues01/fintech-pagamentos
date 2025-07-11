-- Selecionando todos os clientes e todas as faturas apenas para visualização 
SELECT 
	c.*,
	f.*
FROM cliente c
INNER JOIN fatura f ON f.cliente_id = c.id;

-- Clientes status_bloqueio (A=Ativo, B=Bloqueado)
-- Fatura status (P=Paga, A=Atrasada, B=Aberta)

-- Selecionando todos os clientes com mais de 3 dias de atraso no pagamento e status "Bloqueado"
SELECT 
	c.*,
	f.*
FROM cliente c
INNER JOIN fatura f ON f.cliente_id = c.id
WHERE f.status = 'A'
	AND f.data_vencimento < CURRENT_DATE - INTERVAL '3 days' -- Data atual menos 3 dias. Se data_vencimento for menor que esse resultado, é porque está atrasado mais de 3 dias 
	AND c.status_bloqueio = 'B';

-- Atualizando o limite de crédito para zero de clientes bloqueados
UPDATE cliente
SET limite_credito = 0.00
WHERE status_bloqueio = 'B';

