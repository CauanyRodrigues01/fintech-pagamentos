const API_BASE_URL = "http://localhost:8080";

// Lógica de inicialização da página
document.addEventListener("DOMContentLoaded", () => {
  if (document.getElementById("clientes-tbody")) {
    carregarClientes();
    configurarFormularioCliente();
  }
  if (document.getElementById("faturas-tbody")) {
    carregarFaturasCliente();
  }
});

const containerMensagem = document.getElementById("message-container");
let tempoMensagem;

function exibirMensagem(tipo, mensagem) {
  if (tempoMensagem) {
    clearTimeout(tempoMensagem);
  }
  containerMensagem.innerHTML = ""; // Limpa qualquer mensagem anterior

  const alertaDiv = document.createElement("div");
  alertaDiv.classList.add("alert");
  alertaDiv.textContent = mensagem;

  if (tipo === "success") {
    alertaDiv.classList.add("alert-success");
  } else if (tipo === "error") {
    alertaDiv.classList.add("alert-error");
  }

  containerMensagem.appendChild(alertaDiv);

  // Força o reflow para garantir que a transição funcione
  void alertaDiv.offsetWidth;
  alertaDiv.classList.add("visible");

  tempoMensagem = setTimeout(() => {
    alertaDiv.classList.remove("visible");

    alertaDiv.addEventListener("transitionend", function handler(event) {
      if (event.propertyName === "opacity" && alertDiv.style.opacity === "0") {
        containerMensagem.innerHTML = ""; // Limpa o contêiner
        alertDiv.removeEventListener("transitionend", handler);
      }
    });
  }, 5000); // Mensagem visível por 5 segundos
}

// Função para carregar e exibir a lista de clientes
async function carregarClientes() {
  const corpoTabela = document.getElementById("clientes-tbody");
  corpoTabela.innerHTML = ""; // Limpa a tabela

  try {
    const resposta = await fetch(`${API_BASE_URL}/clientes`);
    if (!resposta.ok) {
      throw new Error(`HTTP error! status: ${resposta.status}`);
    }
    const clientes = await resposta.json();

    clientes.forEach((cliente) => {
      const linha = corpoTabela.insertRow();

      // Nome
      const nomeCell = linha.insertCell();
      nomeCell.textContent = cliente.nome;

      // CPF
      const cpfCell = linha.insertCell();
      cpfCell.textContent = cliente.cpf;

      // Idade (Calculado)
      const idadeCell = linha.insertCell();
      idadeCell.textContent = calcularIdade(cliente.dataNascimento);

      // Status de Bloqueio
      const statusCell = linha.insertCell();
      statusCell.textContent =
        cliente.statusBloqueio === "A" ? "Ativo" : "Bloqueado";

      // Limite de Crédito
      const limiteCell = linha.insertCell();
      limiteCell.textContent = `R$ ${cliente.limiteCredito.toFixed(2)}`; // Formata para 2 casas decimais

      // Botões de Ações
      const acoesCell = linha.insertCell();
      const verFaturasBtn = document.createElement("button");
      verFaturasBtn.textContent = "Ver Faturas";
      verFaturasBtn.onclick = () =>
        (window.location.href = `faturas.html?clienteId=${cliente.id}`);
      acoesCell.appendChild(verFaturasBtn);
    });
  } catch (error) {
    console.error("Erro ao carregar clientes:", error);
    exibirMensagem("error", "Erro ao carregar clientes. Verifique o console.");
  }
}

// Função para calcular idade
function calcularIdade(dataDeNascimentoString) {
  const dataNascimentoObj = new Date(dataDeNascimentoString);
  const hoje = new Date();
  let idade = hoje.getFullYear() - dataNascimentoObj.getFullYear();
  const mes = hoje.getMonth() - dataNascimentoObj.getMonth();
  if (mes < 0 || (mes === 0 && hoje.getDate() < dataNascimentoObj.getDate())) {
    idade--;
  }
  return idade;
}

// Função para configurar o formulário de cadastro
function configurarFormularioCliente() {
  const form = document.getElementById("cliente-form");
  form.addEventListener("submit", async (event) => {
    event.preventDefault(); // Evita o recarregamento da página

    const formDados = new FormData(form);
    const clienteData = {};
    formDados.forEach((valorCampo, chaveCampo) => {
      if (chaveCampo === "limiteCredito") {
        clienteData[chaveCampo] = parseFloat(valorCampo); // Converte para número
      } else if (chaveCampo === "dataNascimento") {
        clienteData[chaveCampo] = valorCampo; // Data em formato YYYY-MM-DD
      } else if (chaveCampo === "statusBloqueio") {
        clienteData[chaveCampo] = valorCampo.charAt(0); // Pega apenas o caractere 'A' ou 'B'
      } else {
        clienteData[chaveCampo] = valorCampo;
      }
    });

    try {
      const resposta = await fetch(`${API_BASE_URL}/clientes`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(clienteData),
      });

      if (resposta.status === 400) {
        // Erro de validação
        const errorBody = await resposta.json();
        exibirMensagem(
          "error",
          `Erro de validação: ${errorBody.message}\nDetalhes: ${
            errorBody.details ? errorBody.details.join("\n") : ""
          }`
        );
        console.error("Detalhes do erro de validação:", errorBody);
        return;
      }

      if (!resposta.ok) {
        throw new Error(`HTTP error! status: ${resposta.status}`);
      }

      const novoCliente = await resposta.json();
      exibirMensagem(
        "success",
        `Cliente ${novoCliente.nome} cadastrado com sucesso!`
      );
      form.reset(); // Limpa o formulário
      carregarClientes(); // Recarrega a lista de clientes
    } catch (error) {
      console.error("Erro ao cadastrar cliente:", error);
      exibirMensagem(
        "error",
        "Erro ao cadastrar cliente. Verifique o console."
      );
    }
  });
}

async function carregarFaturasCliente() {
  const parametrosURL = new URLSearchParams(window.location.search);
  const clienteId = parametrosURL.get("clienteId");

  if (!clienteId) {
    exibirMensagem("error", "ID do cliente não fornecido na URL.");
    window.location.href = "index.html"; // Redireciona de volta
    return;
  }

  document.getElementById("cliente-id").textContent = clienteId;

  const corpoTabelaFaturas = document.getElementById("faturas-tbody");
  corpoTabelaFaturas.innerHTML = ""; // Limpa a tabela

  try {
    const resposta = await fetch(`${API_BASE_URL}/faturas/${clienteId}`);
    if (!resposta.ok) {
      throw new Error(`HTTP error! status: ${resposta.status}`);
    }
    const faturas = await resposta.json();

    if (faturas.length > 0) {
      document.getElementById("cliente-nome").textContent =
        faturas[0].clienteNome || "Cliente Desconhecido";
    } else {
      document.getElementById("cliente-nome").textContent =
        "Cliente sem faturas ou Desconhecido";
    }

    faturas.forEach((fatura) => {
      const linha = corpoTabelaFaturas.insertRow();

      // Valor
      const valorCell = linha.insertCell();
      valorCell.textContent = `R$ ${fatura.valor.toFixed(2)}`;

      // Data de Vencimento
      const dataVencimentoCell = linha.insertCell();
      dataVencimentoCell.textContent = fatura.dataVencimento;

      // Status
      const statusCell = linha.insertCell();
      let statusText = "";
      switch (fatura.status) {
        case "P":
          statusText = "Paga";
          break;
        case "A":
          statusText = "Atrasada";
          break;
        case "B":
          statusText = "Aberta";
          break;
        default:
          statusText = "Desconhecido";
      }
      statusCell.textContent = statusText;

      // Data de Pagamento
      const dataPagamentoCell = linha.insertCell();
      dataPagamentoCell.textContent = fatura.dataPagamento || "N/A";

      // Botões de Ações
      const acoesCell = linha.insertCell();
      if (fatura.status !== "P") {
        // Só mostra botão se a fatura não estiver paga
        const pagarBtn = document.createElement("button");
        pagarBtn.textContent = "Registrar Pagamento";
        pagarBtn.onclick = () => registrarPagamento(fatura.id);
        acoesCell.appendChild(pagarBtn);
      }
    });
  } catch (error) {
    console.error("Erro ao carregar faturas:", error);
    exibirMensagem("error", "Erro ao carregar faturas. Verifique o console.");
  }
}

async function registrarPagamento(faturaId) {
  const dataPagamento = new Date().toISOString().split("T")[0]; // Data atual no formato YYYY-MM-DD

  try {
    const resposta = await fetch(
      `${API_BASE_URL}/faturas/${faturaId}/pagamento`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ dataPagamento: dataPagamento }),
      }
    );

    if (resposta.status === 400) {
      // Erro de validação
      const errorBody = await resposta.json();
      exibirMensagem(
        "error",
        `Erro de validação: ${errorBody.message}\nDetalhes: ${
          errorBody.details ? errorBody.details.join("\n") : ""
        }`
      );
      console.error("Detalhes do erro de validação:", errorBody);
      return;
    }

    if (!resposta.ok) {
      throw new Error(`HTTP error! status: ${resposta.status}`);
    }

    const faturaAtualizada = await resposta.json();
    exibirMensagem(
      "success",
      `Fatura ${faturaAtualizada.id} paga com sucesso em ${faturaAtualizada.dataPagamento}!`
    );
    carregarFaturasCliente(); // Recarrega a lista para mostrar o status atualizado
  } catch (error) {
    console.error("Erro ao registrar pagamento:", error);
    exibirMensagem(
      "error",
      "Erro ao registrar pagamento. Verifique o console."
    );
  }
}
