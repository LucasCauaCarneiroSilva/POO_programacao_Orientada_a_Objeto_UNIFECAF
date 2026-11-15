"use strict";
/* Front-end simples: conversa com a API REST (/api/...) usando fetch. */

const $ = (seletor, raiz = document) => raiz.querySelector(seletor);
const $$ = (seletor, raiz = document) => Array.from(raiz.querySelectorAll(seletor));

const estado = { tipos: [], marcas: [], modelos: [], status: [] };
const moeda = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });
const numero = new Intl.NumberFormat("pt-BR");

function esc(valor) {
  return String(valor ?? "").replace(/[&<>"']/g, c =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

// ------------------------------------------------------------------ API
function usuario() {
  return $("#usuario").value.trim() || "sistema";
}

async function api(metodo, url, corpo) {
  const opcoes = { method: metodo, headers: { "X-Usuario": encodeURIComponent(usuario()) } };
  if (corpo !== undefined) {
    opcoes.headers["Content-Type"] = "application/json";
    opcoes.body = JSON.stringify(corpo);
  }
  const resposta = await fetch(url, opcoes);
  if (resposta.status === 204) return null;
  const dados = await resposta.json().catch(() => null);
  if (!resposta.ok) throw new Error((dados && dados.erro) || `Erro ${resposta.status}`);
  return dados;
}

function aviso(texto, tipo = "ok") {
  const caixa = $("#msg");
  caixa.textContent = texto;
  caixa.className = "msg " + tipo;
  caixa.hidden = false;
  clearTimeout(aviso.timer);
  aviso.timer = setTimeout(() => { caixa.hidden = true; }, tipo === "erro" ? 6000 : 3500);
}

/** Executa a ação; mostra o erro da API se falhar. Devolve true se deu certo. */
async function tentar(acao, mensagemOk) {
  try {
    await acao();
    if (mensagemOk) aviso(mensagemOk);
    return true;
  } catch (e) {
    aviso(e.message, "erro");
    return false;
  }
}

// ------------------------------------------------------------------ Abas
$$(".aba").forEach(botao => botao.addEventListener("click", () => abrirAba(botao.dataset.aba)));

function abrirAba(nome) {
  $$(".aba").forEach(b => b.classList.toggle("on", b.dataset.aba === nome));
  $$("main > section").forEach(s => { s.hidden = s.id !== "aba-" + nome; });
  if (nome === "historico") carregarHistorico();
}

// ------------------------------------------------------------------ Catálogos
function preencher(select, lista, rotulo, textoVazio) {
  const atual = select.value;
  const vazio = textoVazio === null ? "" : `<option value="">${esc(textoVazio)}</option>`;
  select.innerHTML = vazio + lista.map(x => `<option value="${esc(x.valor ?? x.id)}">${esc(rotulo(x))}</option>`).join("");
  if ([...select.options].some(o => o.value === atual)) select.value = atual;
}

function modelosDaMarca(marcaId) {
  return marcaId ? estado.modelos.filter(m => String(m.marca.id) === String(marcaId)) : estado.modelos;
}

function preencherSelects() {
  const nome = x => x.nome;
  preencher($("#f-marca"), estado.marcas, nome, "Todas");
  preencher($("#f-tipo"), estado.tipos, nome, "Todos");
  preencher($("#f-status"), estado.status, s => s.rotulo, "Todos");
  preencher($("#v-marca"), estado.marcas, nome, "Selecione...");
  preencher($("#v-status"), estado.status, s => s.rotulo, null);
  preencher($("#m-marca"), estado.marcas, nome, "Selecione...");
  preencher($("#m-tipo"), estado.tipos, nome, "Selecione...");
  atualizarModelosFiltro();
}

function atualizarModelosFiltro() {
  const marcaId = $("#f-marca").value;
  preencher($("#f-modelo"), modelosDaMarca(marcaId), m => marcaId ? m.nome : `${m.marca.nome} ${m.nome}`, "Todos");
}

async function carregarCatalogos() {
  [estado.tipos, estado.marcas, estado.modelos, estado.status] = await Promise.all([
    api("GET", "/api/tipos"), api("GET", "/api/marcas"), api("GET", "/api/modelos"), api("GET", "/api/veiculos/status")
  ]);
  preencherSelects();
  desenharSimples("marcas", estado.marcas);
  desenharSimples("tipos", estado.tipos);
  desenharModelos();
}

/** Botão de excluir em dois cliques (sem janelas de confirmação do navegador). */
function confirmarDuasVezes(botao, acao) {
  if (botao.dataset.armado) {
    acao();
    return;
  }
  botao.dataset.armado = "1";
  const textoOriginal = botao.textContent;
  botao.textContent = "Confirmar exclusão";
  setTimeout(() => { delete botao.dataset.armado; botao.textContent = textoOriginal; }, 3000);
}

/** Tabela de Marcas ou Tipos: renomear na própria linha e excluir. */
function desenharSimples(recurso, lista) {
  const corpo = $("#tb-" + recurso);
  if (!lista.length) {
    corpo.innerHTML = `<tr><td colspan="2" class="vazio">Nada cadastrado.</td></tr>`;
    return;
  }
  corpo.innerHTML = lista.map(x => `
    <tr data-id="${x.id}">
      <td class="nome">${esc(x.nome)}</td>
      <td class="acoes-linha">
        <button type="button" class="lk" data-acao="renomear">Renomear</button>
        <button type="button" class="lk perigo" data-acao="excluir">Excluir</button>
      </td>
    </tr>`).join("");
  $$("tr", corpo).forEach(linha => {
    const id = linha.dataset.id;
    const item = lista.find(x => String(x.id) === id);
    $("[data-acao=renomear]", linha).addEventListener("click", () => {
      $(".nome", linha).innerHTML = `<input class="renomear" value="${esc(item.nome)}">
        <button type="button" class="lk" data-acao="salvar">Salvar</button>
        <button type="button" class="lk" data-acao="cancelar">Cancelar</button>`;
      const campo = $("input", linha);
      campo.focus();
      const salvar = () => tentar(async () => {
        await api("PUT", `/api/${recurso}/${id}`, { nome: campo.value });
        await recarregarTudo();
      }, "Nome alterado.");
      $("[data-acao=salvar]", linha).addEventListener("click", salvar);
      campo.addEventListener("keydown", e => { if (e.key === "Enter") salvar(); });
      $("[data-acao=cancelar]", linha).addEventListener("click", () => desenharSimples(recurso, lista));
    });
    $("[data-acao=excluir]", linha).addEventListener("click", e => confirmarDuasVezes(e.target, () =>
      tentar(async () => {
        await api("DELETE", `/api/${recurso}/${id}`);
        await recarregarTudo();
      }, "Excluído.")));
  });
}

$$("form[data-catalogo]").forEach(form => form.addEventListener("submit", e => {
  e.preventDefault();
  const recurso = form.dataset.catalogo;
  tentar(async () => {
    await api("POST", `/api/${recurso}`, { nome: form.nome.value });
    form.reset();
    await recarregarTudo();
  }, "Cadastrado.");
}));

// ------------------------------------------------------------------ Modelos
function desenharModelos() {
  const corpo = $("#tb-modelos");
  if (!estado.modelos.length) {
    corpo.innerHTML = `<tr><td colspan="4" class="vazio">Nenhum modelo.</td></tr>`;
    return;
  }
  corpo.innerHTML = estado.modelos.map(m => `
    <tr data-id="${m.id}">
      <td>${esc(m.marca.nome)}</td><td>${esc(m.nome)}</td><td>${esc(m.tipo.nome)}</td>
      <td class="acoes-linha">
        <button type="button" class="lk" data-acao="editar">Editar</button>
        <button type="button" class="lk perigo" data-acao="excluir">Excluir</button>
      </td>
    </tr>`).join("");
  $$("tr", corpo).forEach(linha => {
    const modelo = estado.modelos.find(m => String(m.id) === linha.dataset.id);
    $("[data-acao=editar]", linha).addEventListener("click", () => {
      const form = $("#form-modelo");
      form.editId.value = modelo.id;
      form.marcaId.value = modelo.marca.id;
      form.nome.value = modelo.nome;
      form.tipoId.value = modelo.tipo.id;
      $("#bt-modelo").textContent = "Salvar alteração";
      $("#bt-modelo-cancelar").hidden = false;
      form.nome.focus();
    });
    $("[data-acao=excluir]", linha).addEventListener("click", e => confirmarDuasVezes(e.target, () =>
      tentar(async () => {
        await api("DELETE", `/api/modelos/${modelo.id}`);
        await recarregarTudo();
      }, "Modelo excluído.")));
  });
}

function limparFormModelo() {
  const form = $("#form-modelo");
  form.reset();
  form.editId.value = "";
  $("#bt-modelo").textContent = "Adicionar";
  $("#bt-modelo-cancelar").hidden = true;
}

$("#form-modelo").addEventListener("submit", e => {
  e.preventDefault();
  const form = e.target;
  const corpo = { nome: form.nome.value, marcaId: Number(form.marcaId.value), tipoId: Number(form.tipoId.value) };
  const id = form.editId.value;
  tentar(async () => {
    await (id ? api("PUT", `/api/modelos/${id}`, corpo) : api("POST", "/api/modelos", corpo));
    limparFormModelo();
    await recarregarTudo();
  }, id ? "Modelo alterado." : "Modelo cadastrado.");
});
$("#bt-modelo-cancelar").addEventListener("click", limparFormModelo);

// ------------------------------------------------------------------ Veículos
function parametrosFiltro() {
  const dados = new FormData($("#form-filtro"));
  const params = new URLSearchParams();
  for (const [chave, valor] of dados) {
    if (String(valor).trim() !== "") params.append(chave, String(valor).trim());
  }
  return params.toString();
}

async function carregarVeiculos() {
  const consulta = parametrosFiltro();
  const [lista, resumo] = await Promise.all([
    api("GET", "/api/veiculos" + (consulta ? "?" + consulta : "")),
    api("GET", "/api/veiculos/resumo")
  ]);
  desenharKpis(resumo);
  desenharVeiculos(lista);
}

function desenharKpis(r) {
  const itens = [
    ["Veículos cadastrados", numero.format(r.total)],
    ["Disponíveis", numero.format(r.disponiveis)],
    ["Reservados", numero.format(r.reservados)],
    ["Vendidos", numero.format(r.vendidos)],
    ["Descontinuados", numero.format(r.descontinuados)],
    ["Valor em estoque", moeda.format(r.valorEmEstoque)]
  ];
  $("#kpis").innerHTML = itens.map(([rotulo, valor]) =>
    `<div class="kpi"><span class="kr">${rotulo}</span><span class="kv">${valor}</span></div>`).join("");
}

function desenharVeiculos(lista) {
  $("#contagem").textContent = `${lista.length} veículo(s) encontrado(s)`;
  const corpo = $("#tb-veiculos");
  if (!lista.length) {
    corpo.innerHTML = `<tr><td colspan="9" class="vazio">Nenhum veículo com esses filtros.</td></tr>`;
    return;
  }
  const opcoesStatus = atual => estado.status.map(s =>
    `<option value="${s.valor}"${s.valor === atual ? " selected" : ""}>${esc(s.rotulo)}</option>`).join("");
  corpo.innerHTML = lista.map(v => `
    <tr data-id="${v.id}">
      <td>${esc(v.modelo.marca.nome)}</td>
      <td>${esc(v.modelo.nome)}</td>
      <td>${esc(v.modelo.tipo.nome)}</td>
      <td class="n">${v.ano}</td>
      <td>${esc(v.cor)}</td>
      <td class="n">${moeda.format(v.preco)}</td>
      <td class="n">${numero.format(v.quilometragem)}</td>
      <td><select class="st st-${v.status}" aria-label="Status">${opcoesStatus(v.status)}</select></td>
      <td class="acoes-linha">
        <button type="button" class="lk" data-acao="editar">Editar</button>
        <button type="button" class="lk perigo" data-acao="remover">Remover</button>
      </td>
    </tr>`).join("");
  $$("tr", corpo).forEach(linha => {
    const veiculo = lista.find(v => String(v.id) === linha.dataset.id);
    $("select", linha).addEventListener("change", e => tentar(async () => {
      await api("PATCH", `/api/veiculos/${veiculo.id}`, { status: e.target.value });
      await carregarVeiculos();
    }, "Status atualizado."));
    $("[data-acao=editar]", linha).addEventListener("click", () => abrirVeiculo(veiculo));
    $("[data-acao=remover]", linha).addEventListener("click", () => abrirRemocao(veiculo));
  });
}

$("#form-filtro").addEventListener("submit", e => {
  e.preventDefault();
  tentar(carregarVeiculos);
});
$("#bt-limpar").addEventListener("click", () => {
  $("#form-filtro").reset();
  atualizarModelosFiltro();
  tentar(carregarVeiculos);
});
$("#f-marca").addEventListener("change", atualizarModelosFiltro);

// ---- Formulário do veículo
function preencherModelosForm(marcaId, modeloId) {
  preencher($("#v-modelo"), modelosDaMarca(marcaId), m => `${m.nome} (${m.tipo.nome})`, "Selecione...");
  $("#v-modelo").value = modeloId ?? "";
}

function abrirVeiculo(veiculo) {
  const form = $("#form-veiculo");
  form.reset();
  $("#v-erro").hidden = true;
  $("#dlg-veiculo-titulo").textContent = veiculo ? "Editar veículo" : "Novo veículo";
  form.editId.value = veiculo ? veiculo.id : "";
  const marcaId = veiculo ? veiculo.modelo.marca.id : "";
  $("#v-marca").value = marcaId;
  preencherModelosForm(marcaId, veiculo ? veiculo.modelo.id : "");
  form.ano.value = veiculo ? veiculo.ano : new Date().getFullYear();
  form.cor.value = veiculo ? veiculo.cor : "";
  form.preco.value = veiculo ? veiculo.preco : "";
  form.quilometragem.value = veiculo ? veiculo.quilometragem : 0;
  form.status.value = veiculo ? veiculo.status : "DISPONIVEL";
  $("#dlg-veiculo").showModal();
}

$("#bt-novo").addEventListener("click", () => abrirVeiculo(null));
$("#v-marca").addEventListener("change", e => preencherModelosForm(e.target.value, ""));

$("#form-veiculo").addEventListener("submit", async e => {
  e.preventDefault();
  const form = e.target;
  const corpo = {
    modeloId: form.modeloId.value ? Number(form.modeloId.value) : null,
    ano: Number(form.ano.value),
    cor: form.cor.value,
    preco: Number(form.preco.value),
    quilometragem: Number(form.quilometragem.value),
    status: form.status.value
  };
  const id = form.editId.value;
  try {
    await (id ? api("PUT", `/api/veiculos/${id}`, corpo) : api("POST", "/api/veiculos", corpo));
    $("#dlg-veiculo").close();
    aviso(id ? "Veículo atualizado." : "Veículo cadastrado.");
    await carregarVeiculos();
  } catch (erro) {
    const caixa = $("#v-erro");
    caixa.textContent = erro.message;
    caixa.hidden = false;
  }
});

// ---- Remoção
let removendo = null;

function abrirRemocao(veiculo) {
  removendo = veiculo;
  $("#rm-texto").textContent =
    `${veiculo.modelo.marca.nome} ${veiculo.modelo.nome} ${veiculo.ano} ${veiculo.cor} — ${moeda.format(veiculo.preco)}`;
  const form = $("#form-remover");
  form.motivo.value = veiculo.status === "DESCONTINUADO" ? "Descontinuado" : "Vendido";
  $("#dlg-remover").showModal();
}

$("#form-remover").addEventListener("submit", e => {
  e.preventDefault();
  const motivo = e.target.motivo.value;
  $("#dlg-remover").close();
  tentar(async () => {
    await api("DELETE", `/api/veiculos/${removendo.id}?motivo=${encodeURIComponent(motivo)}`);
    await carregarVeiculos();
  }, "Veículo removido do estoque.");
});

$$("[data-fechar]").forEach(b => b.addEventListener("click", () => b.closest("dialog").close()));

// ------------------------------------------------------------------ Histórico
const ACOES = { INCLUSAO: "Inclusão", ALTERACAO: "Alteração", EXCLUSAO: "Exclusão" };

async function carregarHistorico() {
  const entidade = $("#h-entidade").value;
  await tentar(async () => {
    const lista = await api("GET", "/api/historico" + (entidade ? "?entidade=" + encodeURIComponent(entidade) : ""));
    const corpo = $("#tb-historico");
    corpo.innerHTML = lista.length ? lista.map(h => `
      <tr>
        <td>${esc(new Date(h.dataHora).toLocaleString("pt-BR"))}</td>
        <td>${esc(h.usuario)}</td>
        <td>${esc(h.entidade)}</td>
        <td>${esc(h.registro)}</td>
        <td class="ac-${h.acao}">${esc(ACOES[h.acao] || h.acao)}</td>
        <td class="detalhe">${esc(h.detalhes)}</td>
        <td>${esc(h.motivo)}</td>
      </tr>`).join("") : `<tr><td colspan="7" class="vazio">Sem alterações registradas.</td></tr>`;
  });
}
$("#h-entidade").addEventListener("change", carregarHistorico);

// ------------------------------------------------------------------ Início
async function recarregarTudo() {
  await carregarCatalogos();
  await carregarVeiculos();
}

(function iniciar() {
  const campo = $("#usuario");
  try { campo.value = localStorage.getItem("estoque.usuario") || ""; } catch (e) { /* sem storage */ }
  campo.addEventListener("change", () => {
    try { localStorage.setItem("estoque.usuario", campo.value.trim()); } catch (e) { /* sem storage */ }
  });
  tentar(recarregarTudo).then(ok => {
    if (!ok) aviso("Não foi possível falar com o servidor. Ele está rodando em http://localhost:8080?", "erro");
  });
})();
