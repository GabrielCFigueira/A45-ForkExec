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


    \newpage

# Instalação e Configuração do projeto

0. Buscar o código entregue

```
git clone https://github.com/tecnico-distsys/A45-ForkExec.git
git checkout SD_P2
```

1. Instalar módulos

```
cd A45-ForkExec/
mvn install -DskipTests (TODO: ou ver -Dmaven-test)
```

2. Colocar 3 servidores a correr (variar `N` entre 1 e 3):

```
cd pts-ws/
mvn exec:java -Dws.i=N
```

# Demonstrações

## F1 -- Funcionamento normal

3. Correr o seguinte comando

```
cd pts-ws-cli/
mvn exec:java -Dws.test=F1
```


## F2 -- Funcionamento em casos de falta

3. Correr o seguinte comando

```
cd pts-ws-cli/
mvn exec:java -Dws.test=F2
```