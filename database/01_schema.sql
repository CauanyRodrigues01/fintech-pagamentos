-- Garante que o banco de dados esteja no estado correto para criação das tabelas
DROP TABLE IF EXISTS Fatura;
DROP TABLE IF EXISTS Cliente;

CREATE TABLE Cliente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL, -- CPF com 11 dígitos
    data_nascimento DATE NOT NULL,
    status_bloqueio CHAR(1) NOT NULL DEFAULT 'A', -- 'A' para Ativo, 'B' para Bloqueado
    limite_credito NUMERIC(10,2) NOT NULL,

    CONSTRAINT chk_status_bloqueio CHECK (status_bloqueio IN ('A', 'B'))
);

CREATE TABLE Fatura (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), 
    cliente_id UUID NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE, 
    valor NUMERIC(10,2) NOT NULL,
    status CHAR(1) NOT NULL DEFAULT 'B', -- 'P'=Paga, 'A'=Atrasada, 'B'=Aberta

    CONSTRAINT chk_fatura_status CHECK (status IN ('P', 'A', 'B')),

    CONSTRAINT fk_fatura_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES Cliente (id)
        ON DELETE RESTRICT -- Não pode excluir um cliente se houver faturas associadas a ele
);
