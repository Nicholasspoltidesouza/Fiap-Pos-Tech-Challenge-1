# Event Storming - Oficina Mecânica

Evolução do modelo a partir do Event Storming, seguindo as etapas: **brainstorm de eventos →
linha do tempo → eventos pivotais → comandos/políticas/agregados → contextos delimitados**.

Legenda das cores (notação clássica):

- 🟧 **Evento de domínio** (fato no passado)
- 🟦 **Comando** (intenção/ação)
- 🟨 **Agregado** (onde a regra é aplicada)
- 🟪 **Política** (reação automática: "sempre que... então...")
- 🟩 **Read model** (informação para leitura/decisão)
- 🟫 **Ator**
- 🌸 **Sistema externo**

## 1. Brainstorm de eventos (não ordenado)

`OrdemServicoAberta`, `DiagnosticoIniciado`, `OrcamentoCalculado`, `OrcamentoEnviado`,
`OrcamentoAprovado`, `OrcamentoReprovado`, `EstoqueBaixado`, `ServicosFinalizados`,
`VeiculoEntregue`, `ClienteCadastrado`, `VeiculoCadastrado`, `PecaAdicionadaNaOrdem`,
`NotificacaoEnviada`, `AcompanhamentoConsultado`.

## 2. Linha do tempo (eventos ordenados)

```mermaid
flowchart LR
    A[🟧 OrdemServicoAberta] --> B[🟧 DiagnosticoIniciado]
    B --> C[🟧 OrcamentoCalculado]
    C --> D[🟧 OrcamentoEnviado]
    D --> E{Cliente decide}
    E -->|aprova| F[🟧 OrcamentoAprovado]
    E -->|reprova| G[🟧 OrcamentoReprovado]
    F --> H[🟧 EstoqueBaixado]
    H --> I[🟧 ServicosFinalizados]
    I --> J[🟧 VeiculoEntregue]
    G -.volta para RECEBIDA.-> A
```

## 3. Eventos pivotais (mudam o estado/fase do negócio)

Os eventos pivotais marcam a transição entre fases e correspondem às transições de estado do agregado:

- ⭐ **OrcamentoEnviado** — separa "diagnóstico" de "negociação com o cliente".
- ⭐ **OrcamentoAprovado** — separa "negociação" de "execução" (e dispara baixa de estoque).
- ⭐ **ServicosFinalizados** — separa "execução" de "entrega".
- ⭐ **VeiculoEntregue** — encerra o ciclo da OS.

```mermaid
stateDiagram-v2
    [*] --> RECEBIDA
    RECEBIDA --> EM_DIAGNOSTICO: iniciarDiagnostico
    EM_DIAGNOSTICO --> AGUARDANDO_APROVACAO: enviarOrcamento ⭐
    AGUARDANDO_APROVACAO --> EM_EXECUCAO: aprovarOrcamento(true) ⭐
    AGUARDANDO_APROVACAO --> RECEBIDA: aprovarOrcamento(false)
    EM_EXECUCAO --> FINALIZADA: finalizar ⭐
    FINALIZADA --> ENTREGUE: entregar ⭐
    ENTREGUE --> [*]
```

## 4. Comandos, atores, políticas e agregados

```mermaid
flowchart TB
    subgraph Atendente_ctx [🟫 Atendente]
        c1[🟦 AbrirOrdemServico]
        c6[🟦 EntregarVeiculo]
    end
    subgraph Mecanico_ctx [🟫 Mecânico]
        c2[🟦 IniciarDiagnostico]
        c3[🟦 EnviarOrcamento]
        c5[🟦 FinalizarServicos]
    end
    subgraph Cliente_ctx [🟫 Cliente]
        c4[🟦 AprovarOrcamento]
        c7[🟦 ConsultarAcompanhamento]
    end

    AG[🟨 OrdemServico - Aggregate Root]

    c1 --> AG --> e1[🟧 OrdemServicoAberta]
    c2 --> AG --> e2[🟧 DiagnosticoIniciado]
    c3 --> AG --> e3[🟧 OrcamentoEnviado]
    c4 --> AG --> e4[🟧 OrcamentoAprovado]
    c5 --> AG --> e5[🟧 ServicosFinalizados]
    c6 --> AG --> e6[🟧 VeiculoEntregue]

    e3 --> p1[🟪 Política: notificar cliente por e-mail]
    p1 --> mail[🌸 Serviço de E-mail/MailHog]
    e4 --> p2[🟪 Política: baixar estoque de peças e insumos]
    p2 --> EST[🟨 Estoque - Peca/Insumo]

    c7 --> RM[🟩 Read Model: Acompanhamento da OS]
```

### Políticas (reações automáticas)

- **Sempre que** um `OrcamentoEnviado` ocorre, **então** o sistema envia uma notificação por e-mail ao cliente.
- **Sempre que** um `OrcamentoAprovado` ocorre, **então** o sistema baixa o estoque das peças e insumos vinculados.
- **Sempre que** um status muda (`*`), **então** o sistema notifica a atualização por e-mail.

## 5. Contextos delimitados (Bounded Contexts)

```mermaid
flowchart LR
    subgraph Oficina [Contexto: Oficina - Core]
        OS[🟨 OrdemServico]
        Cli[Cliente]
        Vei[Veiculo]
        Ser[Servico]
    end
    subgraph Estoque [Contexto: Estoque]
        Pec[🟨 Peca]
        Ins[🟨 Insumo]
    end
    subgraph Notificacao [Contexto: Notificação]
        Gw[NotificacaoOrdemServicoGateway]
    end
    subgraph Identidade [Contexto: Identidade & Acesso]
        Usr[Usuario]
        Jwt[JWT / Perfis]
    end

    OS -->|consome itens| Estoque
    OS -->|publica mudança de status| Notificacao
    Identidade -->|autoriza ações| Oficina
```

### Mapa de relacionamento

| Contexto | Responsabilidade | Relação |
|---|---|---|
| **Oficina (Core)** | Ciclo de vida da OS, cliente, veículo, serviços. | Núcleo do negócio. |
| **Estoque** | Disponibilidade e baixa de peças/insumos. | Fornecedor (upstream) consumido pela OS. |
| **Notificação** | Entrega de e-mails de mudança de status. | Conformista a jusante (downstream), acionado por política. |
| **Identidade & Acesso** | Usuários, autenticação JWT e perfis. | Suporte/cross-cutting que autoriza comandos. |

## 6. Do Event Storming ao código

| Conceito do Event Storming | Implementação |
|---|---|
| Agregado `OrdemServico` | `domain/model/OrdemServico.java` (máquina de estados rica) |
| Comandos de transição | métodos `iniciarDiagnostico()`, `enviarOrcamento()`, `aprovarOrcamento()`, `finalizar()`, `entregar()` |
| Eventos pivotais | mudanças de `StatusOrdemServico` validadas no agregado |
| Política de notificação | `NotificacaoOrdemServicoGateway` + `EmailNotificacaoGatewayImpl` |
| Política de baixa de estoque | `OrdemServicoServiceUsecaseImpl.consumirEstoque*` |
| Value Objects | `domain/model/vo/CpfCnpj.java`, `domain/model/vo/Placa.java` |
| Exceções de domínio | `DomainException`, `TransicaoStatusInvalidaException` |
