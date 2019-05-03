---
title: ForkExec -- Guião de demonstração do grupo A45
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


# Instalação e Configuração do projeto

0. Buscar o código entregue

```
git clone https://github.com/tecnico-distsys/A45-ForkExec.git
cd A45-ForkExec
git checkout SD_P2
```

1. Instalar módulos

```
mvn install -DskipTests
```

2. Colocar 3 servidores a correr (variar `N` entre 1 e 3):

```
cd pts-ws/
mvn exec:java -Dws.i=N
```

# Demonstrações

3. Para cada um dos seguintes testes, correr os seguintes comandos:

```
cd pts-ws-cli/
mvn exec:java -Dws.test=FX
```

Por exemplo, para a primeira demonstração, seria `mvn exec:java -Dws.test=F1`


  - F1: Funcionamento normal (pointsBalance)
  - F2: Funcionamento em caso de delay
  - F3: Demonstração do mecanismo da cache -- só uma leitura é realizada às réplicas,
        mas o método `pointsBalance` é invocado 1000 vezes
  - F4: Demonstração do mecanismo da cache -- o método `pointsBalance` é invocado 10 vezes,
        mas sem o mecanismo de cache, aumentando o tempo de teste.
  - F5: Demonstração da concorrência de _reads_ no mesmo _user_
  - F6: Demonstração da concorrência de _writes_ no mesmo _user_
  - F7: Demonstração da concorrência de _reads_ em diferentes _users_
  - F8: Demonstração da concorrência de _writes_ em diferentes _users_