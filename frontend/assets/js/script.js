const API_BASE_URL = 'http://localhost:8080'; // URL da sua API Spring Boot

document.addEventListener('DOMContentLoaded', () => {
    loadClients(); // Carrega clientes ao carregar a página
    setupClientForm(); // Configura o formulário de cadastro de cliente
});

// Função para carregar e exibir a lista de clientes
async function loadClients() {
    const tbody = document.getElementById('clients-tbody');
    tbody.innerHTML = ''; // Limpa a tabela

    try {
        const response = await fetch(`${API_BASE_URL}/clientes`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const clients = await response.json();

        clients.forEach(client => {
            const row = tbody.insertRow();

            // Nome
            const nameCell = row.insertCell();
            nameCell.textContent = client.nome;

            // CPF
            const cpfCell = row.insertCell();
            cpfCell.textContent = client.cpf;

            // Idade (Calculado)
            const ageCell = row.insertCell();
            ageCell.textContent = calculateAge(client.dataNascimento); // Função a ser criada

            // Status de Bloqueio
            const statusCell = row.insertCell();
            statusCell.textContent = client.statusBloqueio === 'A' ? 'Ativo' : 'Bloqueado';

            // Limite de Crédito
            const limitCell = row.insertCell();
            limitCell.textContent = `R$ ${client.limiteCredito.toFixed(2)}`; // Formata para 2 casas decimais

            // Botões de Ações
            const actionsCell = row.insertCell();
            const viewFaturasBtn = document.createElement('button');
            viewFaturasBtn.textContent = 'Ver Faturas';
            viewFaturasBtn.onclick = () => window.location.href = `faturas.html?clienteId=${client.id}`;
            actionsCell.appendChild(viewFaturasBtn);
        });
    } catch (error) {
        console.error('Erro ao carregar clientes:', error);
        alert('Erro ao carregar clientes. Verifique o console.');
    }
}

// Função para calcular idade
function calculateAge(dateOfBirth) {
    const birthDate = new Date(dateOfBirth);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const m = today.getMonth() - birthDate.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
        age--;
    }
    return age;
}

// Função para configurar o formulário de cadastro
function setupClientForm() {
    const form = document.getElementById('client-form');
    form.addEventListener('submit', async (event) => {
        event.preventDefault(); // Evita o recarregamento da página

        const formData = new FormData(form);
        const clientData = {};
        formData.forEach((value, key) => {
            if (key === 'limiteCredito') {
                clientData[key] = parseFloat(value); // Converte para número
            } else if (key === 'dataNascimento') {
                clientData[key] = value; // Data em formato YYYY-MM-DD
            } else if (key === 'statusBloqueio') {
                clientData[key] = value.charAt(0); // Pega apenas o caractere 'A' ou 'B'
            } else {
                clientData[key] = value;
            }
        });

        try {
            const response = await fetch(`${API_BASE_URL}/clientes`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(clientData)
            });

            if (response.status === 400) { // Erro de validação
                const errorBody = await response.json();
                alert(`Erro de validação: ${errorBody.message}\nDetalhes: ${errorBody.details ? errorBody.details.join('\n') : ''}`);
                console.error('Detalhes do erro de validação:', errorBody);
                return;
            }

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const newClient = await response.json();
            alert(`Cliente ${newClient.nome} cadastrado com sucesso!`);
            form.reset(); // Limpa o formulário
            loadClients(); // Recarrega a lista de clientes
        } catch (error) {
            console.error('Erro ao cadastrar cliente:', error);
            alert('Erro ao cadastrar cliente. Verifique o console.');
        }
    });
}


document.addEventListener('DOMContentLoaded', () => {
    // Para index.html
    if (document.getElementById('clients-tbody')) {
        loadClients();
        setupClientForm();
    }
    // Para faturas.html
    if (document.getElementById('invoices-tbody')) {
        loadClientInvoices();
    }
});

async function loadClientInvoices() {
    const urlParams = new URLSearchParams(window.location.search);
    const clientId = urlParams.get('clienteId');

    if (!clientId) {
        alert('ID do cliente não fornecido na URL.');
        window.location.href = 'index.html'; // Redireciona de volta
        return;
    }

    document.getElementById('client-id').textContent = clientId;

    const tbody = document.getElementById('invoices-tbody');
    tbody.innerHTML = ''; // Limpa a tabela

    try {
        const response = await fetch(`${API_BASE_URL}/faturas/${clientId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const invoices = await response.json();

        if (invoices.length > 0) {
            document.getElementById('client-name').textContent = invoices[0].clienteNome || 'Cliente Desconhecido';
        } else {
            document.getElementById('client-name').textContent = 'Cliente sem faturas ou Desconhecido';
        }

        invoices.forEach(invoice => {
            const row = tbody.insertRow();

            // Valor
            const valueCell = row.insertCell();
            valueCell.textContent = `R$ ${invoice.valor.toFixed(2)}`;

            // Data de Vencimento
            const dueDateCell = row.insertCell();
            dueDateCell.textContent = invoice.dataVencimento;

            // Status
            const statusCell = row.insertCell();
            let statusText = '';
            switch(invoice.status) {
                case 'P': statusText = 'Paga'; break;
                case 'A': statusText = 'Atrasada'; break;
                case 'B': statusText = 'Aberta'; break;
                default: statusText = 'Desconhecido';
            }
            statusCell.textContent = statusText;

            // Data de Pagamento
            const paymentDateCell = row.insertCell();
            paymentDateCell.textContent = invoice.dataPagamento || 'N/A';

            // Botões de Ações
            const actionsCell = row.insertCell();
            if (invoice.status !== 'P') { // Só mostra botão se a fatura não estiver paga
                const payBtn = document.createElement('button');
                payBtn.textContent = 'Registrar Pagamento';
                payBtn.onclick = () => registerPayment(invoice.id);
                actionsCell.appendChild(payBtn);
            }
        });

    } catch (error) {
        console.error('Erro ao carregar faturas:', error);
        alert('Erro ao carregar faturas. Verifique o console.');
    }
}

async function registerPayment(invoiceId) {
    const paymentDate = new Date().toISOString().split('T')[0]; // Data atual no formato YYYY-MM-DD

    try {
        const response = await fetch(`${API_BASE_URL}/faturas/${invoiceId}/pagamento`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ dataPagamento: paymentDate })
        });

        if (response.status === 400) { // Erro de validação
            const errorBody = await response.json();
            alert(`Erro de validação: ${errorBody.message}\nDetalhes: ${errorBody.details ? errorBody.details.join('\n') : ''}`);
            console.error('Detalhes do erro de validação:', errorBody);
            return;
        }

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const updatedInvoice = await response.json();
        alert(`Fatura ${updatedInvoice.id} paga com sucesso em ${updatedInvoice.dataPagamento}!`);
        loadClientInvoices(); // Recarrega a lista para mostrar o status atualizado
    } catch (error) {
        console.error('Erro ao registrar pagamento:', error);
        alert('Erro ao registrar pagamento. Verifique o console.');
    }
}