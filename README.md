# POO_programacao_Orientada_a_Objeto_UNIFECAF

# Sistema Automotivo — Gestão de Estoque de Veículos

CRUD orientado a objetos em **Java 17 + Spring Boot 4.1 + MySQL 8**, com API REST e uma tela web simples
(HTML, CSS e JavaScript servidos pelo próprio Spring Boot).

---

## Guia de instalação e execução

### 1. Instalar o que é necessário

| Programa | Para que serve | Onde conseguir |
|---|---|---|
| **IntelliJ IDEA** | Editar e rodar o projeto | jetbrains.com/idea |
| **JDK 17** (ou 21) | Compilar e executar Java | Pelo próprio IntelliJ (passo 3) |
| **MySQL 8** + **Workbench** | Banco de dados | dev.mysql.com/downloads/installer |

Não é preciso instalar o Maven nem o Spring: o projeto traz o Maven Wrapper (`mvnw`), que baixa tudo sozinho.

No instalador do MySQL, escolha Server e Workbench e anote a senha definida para o usuário root.

2. Criar o banco de dados
   Abra o MySQL Workbench e conecte em localhost com o usuário root.
   Menu File → Open SQL Script → escolha banco/estoque_veiculos.sql.
   Clique no raio (Execute) para rodar o script inteiro.
   No painel Schemas, clique em atualizar: deve aparecer o banco estoque_veiculos com 5 tabelas.

O script cria as tabelas, os dados de exemplo (5 tipos, 6 marcas, 10 modelos e 11 veículos) e o usuário estoque, que a aplicação usa para conectar. Rodar de novo apaga tudo e volta ao estado inicial.

### 3. Abrir o projeto no IntelliJ

1. **File → Open** → selecione a pasta do projeto completo → **Trust Project**.
2. Espere o IntelliJ baixar as dependências.
3. Se aparecer "Project JDK is not defined": **File → Project Structure → SDK → Add SDK → Download JDK**,
   escolha a versão 17 (ou 21) e confirme.

### 4. Configurar a senha do banco

1. Abra `src/main/resources/application.properties`

2. cria o usuário estoque (senha estoque123), que a aplicação usa para conectar. Não é preciso colocar a senha do root no projeto.

Só confira a porta: no Workbench, a conexão mostra a porta do MySQL (normalmente 3306). Se for outra, troque o número em src/main/resources/application.properties:

### 5. Rodar o sistema

1. Abra `src/main/java/com/example/demo/DemoApplication.java`.
2. Se aparecer a mensangem **project JDK is not defined**, clique em **Setud SDK** → **Download JDK** → **Download JDK**; escolha a versão 17 (ou 21) e confirme.
3. Clique no **▶** verde ao lado do `main` → **Run 'DemoApplication'**. 
4. Espere a mensagem `Started DemoApplication` no console.
5. Abra o navegador em **http://localhost:8080**.

### 6. Usar o sistema

| Operação | Como fazer na tela |
|---|---|
| **Listar** | Aba **Veículos**: tabela com todos os veículos e o resumo do estoque no topo |
| **Filtrar** | Preencha marca, modelo, tipo, status, ano, preço, cor ou busca livre → **Filtrar** |
| **Cadastrar** | **+ Novo veículo** → preencha → **Salvar** |
| **Atualizar** | **Editar** na linha (todos os campos) ou troque o status direto na tabela |
| **Remover** | **Remover** na linha → escolha o motivo (Vendido, Descontinuado…) |
| **Marcas / Modelos / Tipos** | Abas próprias: adicionar, renomear/editar e excluir |
| **Histórico** | Aba **Histórico**: quem alterou, o que mudou (antes → depois) e o motivo |

Teste também os erros: preço zero, ano 1800, excluir uma marca que tem modelos. O sistema mostra a mensagem
e não grava nada.

### 7. Rodar os testes

- Painel **Maven** (lateral direita) → **Lifecycle** → **test** (duplo clique), ou
- botão direito em `src/test/java` → **Run 'All Tests'**.

Os testes de `model`, `dto` e `util` não precisam de banco. O `DemoApplicationTests` (gerado pelo Initializr)
sobe a aplicação inteira, então precisa do MySQL ligado e do script já aplicado.

### 8. Testar a API (opcional)

As consultas `GET` abrem direto no navegador, por exemplo:

- http://localhost:8080/api/veiculos?status=DISPONIVEL&precoMax=100000
- http://localhost:8080/api/veiculos/resumo

Exemplos de todas as chamadas (POST, PUT, PATCH, DELETE) estão em `docs/api-exemplos.http`. Rode-os pelo
Postman ou Insomnia, ou direto no IntelliJ se a sua versão tiver o *HTTP Client*.


### Problemas comuns

| Mensagem | Causa | Solução |
|---|---|---|
| `Access denied for user 'root'` | Senha errada | Corrija a senha no `application.properties` (passo 4) |
| `Communications link failure` | MySQL desligado | Inicie o serviço MySQL (Workbench ou *Serviços* do Windows) |
| `Unknown database 'estoque_veiculos'` | Script não rodado | Rode `banco/estoque_veiculos.sql` (passo 2) |
| `Port 8080 was already in use` | Outra aplicação na porta | Feche a outra ou troque `server.port` no `application.properties` |
| Tela abre sem dados | API não respondeu | Veja o erro no console do IntelliJ |

---

## Estrutura do projeto

```
src/main/java/com/example/demo
├── DemoApplication.java   ponto de entrada (main)
├── model/        classes do domínio: Auditavel (interface), EntidadeBase (abstrata),
│                 TipoVeiculo, Marca, Modelo, Veiculo, StatusVeiculo (enum), HistoricoAlteracao
├── repository/   acesso ao banco (Spring Data JPA)
├── service/      regras de negócio
├── controller/   API REST e tratamento de erros
├── dto/          dados de entrada da API, filtro e resumo
├── util/         validação, busca sem acento, comparação antes/depois
└── excecao/      exceções de regra de negócio (400) e não encontrado (404)
src/main/resources/static   tela web (index.html, css, js)
banco/estoque_veiculos.sql  modelo físico + dados iniciais
docs/api-exemplos.http      exemplos de chamadas da API
```

Arquitetura em camadas (MVC/REST): **controller** recebe a requisição → **service** aplica as regras →
**repository** lê e grava no MySQL → **model** representa os dados.

## Conceitos de POO aplicados

| Conceito | Onde |
|---|---|
| Classes e objetos | `Veiculo`, `Modelo`, `Marca`, `TipoVeiculo` |
| Encapsulamento | Atributos `private`; setters validam (ex.: `setPreco` recusa zero ou negativo) |
| Construtores e sobrecarga | `Veiculo` tem um construtor com status e outro sem (usa `this(...)`) |
| Herança | As quatro entidades fazem `extends EntidadeBase` |
| Classe abstrata | `EntidadeBase` |
| Interface | `Auditavel`, implementada por `EntidadeBase`; repositórios do Spring |
| Sobrescrita e polimorfismo | `@Override` em `descricao()`, `snapshot()` e `toString()`; o histórico trata tudo como `Auditavel` |
| Associação | `Veiculo` → `Modelo` → `Marca` e `TipoVeiculo` |
| Enum | `StatusVeiculo` (Disponível, Reservado, Vendido, Descontinuado) |
