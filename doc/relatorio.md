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


Na nossa implementação, as falhas toleradas de gestores de réplica são:
  - Não estar inscrito no _UDDI_, não sendo portanto contactáveis
  - Demorarem tempo indeterminado, mas limitado, a responder

Ou seja, se o gestor de réplica estiver registado no _UDDI_ e for possível
criar um `PointsClient`, então ele tem de eventualmente responder.

# Solução de tolerância de faltas

TODO: Add image

Usar o _Quorum Consensus_, onde:

  - Cada gestor de réplica é um servidor de pontos com a informação de
    todos os clientes do _Hub_.
  - O _Hub_ tem um objeto `PointsFrontEnd`, que implementa o protocolo
    _Quorum Consensus_ para os seus clientes, e também implementa a antiga
    interface de `PointsClient`[^exc], minimizando assim as alterações
    necessárias ao código do _Hub_.
  - O _Front End_ garante que só há **um** acesso (leitura ou escrita) para o
    valor de pontos para cada cliente, e portanto os servidores de pontos
    não terão mecanismos para garantir que não há acessos concorrentes do
    mesmo valor[^chm] (i.e., a consistência sequencial do protocolo é
    assegurado somente no _Front End_).


[^exc]: Na implementação, foi necessário alterar as exceções, e alguns testes,
        uma vez que não podemos assumir que um utilizador não existe só porque
        não se encontra registado nas múltiplas réplicas.

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
  - O _Front End_ pode fazer vários pedidos assíncronos a um mesmo servidor de
    pontos, mas os ditos pedidos são acerca de clientes diferentes.
    O servidor de pontos pode tratar destes pedidos de forma paralela.
  - O _Front End_ tem um mecanismo de _cache_, que minimiza os acessos
    necessários aos gestores de réplicas. Sendo assim, estes (os
    gestores de réplica) funcionam como _fallback_, caso o valor pretendido
    não esteja na dita _cache_. Caso houvesse a possibilidade de haver 2
    _Front Ends_ a realizarem operações ao mesmo tempo, seria necessário
    implementar um mecanismo de invalidação de cache.
  - Caso o _Front End_ detete que só consegue contactar uma minoria de
    gestores de réplica (o que não é tolerável de acordo com o nosso
    modelo de faltas), é mandada uma exceção e a operação não é realizada.

# Protocolo

TODO: Mais granular

Remoção de pontos:

  - o FrontEnd envia pedidos de leitura aos `N` servidores de pontos
  - cada servidor de pontos responde com os números de pontos e versão
    armazenados para esse utilizador
  - o FrontEnd espera por `(N+1)/2` respostas, e vai guardando o maior
    número de versão observado e respetivo valor.
  - depois de ter as respostas, o FrontEnd verifica que o balance é suficiente,
    senão lança uma exceção
  - se puder, subtrai os pontos, incrementa por 1 a versão e manda `N` pedidos de escrita
  - Após ter `(N+1)/2` confirmações, retorna.