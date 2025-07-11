-- Inserir Clientes

INSERT INTO cliente (nome, cpf, data_nascimento, status_bloqueio, limite_credito) VALUES
('João Silva', '12345678901', '1990-01-15', 'A', 5000.00), -- Ativo
('Maria Oliveira', '23456789012', '1985-04-20', 'A', 3000.00), -- Ativo
('Carlos Souza', '34567890123', '1978-09-10', 'A', 1000.00), -- Ativo
('Ana Pereira', '45678901234', '1995-07-25','B', 500.00), -- BLOQUEADA 
('Paulo Santos', '56789012345', '1982-12-05', 'A', 2000.00), -- Ativo
('Luciana Rocha', '67890123456', '1998-03-12', 'A', 4500.00), -- Ativo
('Marcos Lima', '78901234567', '1987-06-18','B', 500.00), -- BLOQUEADO
('Fernanda Alves', '89012345678', '1993-11-30', 'A', 2500.00), -- Ativo
('Rodrigo Teixeira', '90123456789', '1975-05-07', 'B', 1500.00), -- BLOQUEADO
('Juliana Mendes', '01234567890', '2000-08-22', 'A', 3500.00); -- Ativo

SELECT * FROM cliente;

-- Inserir Faturas relacionadas aos clientes
INSERT INTO fatura (cliente_id, data_vencimento, data_pagamento, valor, status) VALUES

-- 1. Faturas PAGAS ('P') - data_pagamento preenchida, status 'P'
-- Cliente: João Silva (Ativo)
('cd3b135b-3c8f-4113-908b-17a5b5e33aad', CURRENT_DATE - INTERVAL '12 days', CURRENT_DATE - INTERVAL '5 days', 300.00, 'P'),
-- Cliente: Paulo Santos (Ativo)
('a0fc63ff-13ab-4206-ba67-dc3f592de137', CURRENT_DATE - INTERVAL '20 days', CURRENT_DATE - INTERVAL '10 days', 150.00, 'P'),
-- Cliente: Fernanda Alves (Ativo)
('27b3fac3-1c86-45f4-9f22-03926d456e53', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE - INTERVAL '2 days', 100.00, 'P'),


-- 2. Faturas ABERTAS ('B') - data_pagamento NULL, data_vencimento no futuro ou muito próximo
-- Cliente: Carlos Souza (Ativo)
('cdb035bf-e8ef-4143-9416-abfafa1e8d8d', CURRENT_DATE + INTERVAL '10 days', NULL, 200.00, 'B'),
-- Cliente: Luciana Rocha (Ativo)
('c15ae1c2-2d59-41bf-8c99-21cadc69c722', CURRENT_DATE + INTERVAL '5 days', NULL, 600.00, 'B'),
-- Cliente: Juliana Mendes (Ativo)
('5abffe13-97df-45dd-bfce-06999b7b4020', CURRENT_DATE + INTERVAL '3 days', NULL, 450.00, 'B'),

-- 3. Faturas ATRASADAS ('A') - data_pagamento NULL, data_vencimento no passado
-- Cliente: Maria Oliveira (Ativo, fatura com ATRASO < 3 dias)
('97278ef1-8dff-4050-9029-1bc057522fe6', CURRENT_DATE - INTERVAL '2 days', NULL, 500.00, 'A'), -- Vencida há 2 dias
-- Cliente: Rodrigo Teixeira (Bloqueado, fatura com ATRASO > 3 dias)
('6d964a04-75aa-49e7-b6e2-b92864d23e4a', CURRENT_DATE - INTERVAL '6 days', NULL, 550.00, 'A'), -- Vencida há 6 dias (maior que 3)
-- Cliente: Marcos Lima (Bloqueado, fatura com ATRASO > 3 dias)
('58d0a6e7-46d7-4f5b-a81f-2b46df64fe2d', CURRENT_DATE - INTERVAL '7 days', NULL, 250.00, 'A'), -- Vencida há 7 dias (maior que 3)
-- Cliente: Ana Pereira (Bloqueado, fatura com ATRASO > 3 dias)
('d850c4f1-9efc-4d03-b662-af9d29bfebfa', CURRENT_DATE - INTERVAL '15 days', NULL, 400.00, 'A');  -- Vencida há 15 dias (maior que 3)


SELECT * FROM fatura;