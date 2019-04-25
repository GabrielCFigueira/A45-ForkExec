---
title: ForkExec -- Relatório do grupo A45
subtitle: https://github.com/tecnico-distsys/A45-ForkExec
documentclass: article
author:
- Gabriel Figueira, 86426
- Lívio Costa, 86461
- Rafael Andrade, 86503
papersize: A4
fontsize: 14pt
geometry: margin=3cm
---

```
\newpage
```

# Modelo de faltas

Segundo o enunciado:

  + Os gestores de réplica podem falhar silenciosamente mas não
    arbitrariamente, i.e., não há falhas bizantinas;
  + No máximo, existe uma minoria de gestores de réplica em falha em
    simultâneo;
  + O sistema é assíncrono e a comunicação pode omitir mensagens
    (apesar do projeto usar HTTP como transporte, deve assumir-se que
    outros protocolos de menor fiabilidade podem ser usados)


# Solução de tolerância de faltas

Usar o _Quorum Consensus_, onde:

  - Cada gestor de réplica é um servidor de pontos com a informação de
    todos os clientes do _Hub_
  - O _Hub_ é o _Front End_ para os seus clientes
  - O _Hub_ garante que só há **um** acesso (leitura ou escrita) para o
    valor de pontos para cada cliente, e portanto os servidores de pontos
    não terão mecanismos para garantir que não há acessos concorrentes do
    mesmo valor[^chm] (i.e., a consistência sequencial do protocolo é
    assegurado somente no _Hub_)

[^chm]: Em termos de implementação, existe alguma segurança em relação
        a acessos concorrentes, por causa da utilização de
        `ConcurrentHashMap<>`. No entanto, o restante código não evita
       eventuais _race conditions_ na utilização dos valores após serem
       lidos do dito `Map<>`.

Este protocolo garante consistência sequencial.

# Optimizações & Simplificações

  - As mensagens trocadas entre o _Front End_ e os gestores de réplica
    contêm o número de pontos e um número de sequência. Não existe
    campo para identificar o _Front End_, porque neste caso o _Front End_
    é único.
  - O _Hub_ pode fazer vários pedidos assíncronos a um mesmo servidor de
    pontos, mas os ditos pedidos são acerca de clientes diferentes.
    O servidor de pontos pode tratar destes pedidos de forma paralela.

# Protocolo
