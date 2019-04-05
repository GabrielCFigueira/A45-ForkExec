# ForkExec

**Fork**: delicious menus for **Exec**utives

Distributed Systems 2018-2019, 2nd semester project

## Authors

Group A45

Gabriel Costa Figueira 86426 GabrielCFigueira

Lívio Costa 86461 LivioCosta

Rafael Pestana de Andrade 86503 RafaelPAndrade


For each module, the README file must identify the lead developer and the contributors.
The leads should be evenly divided among the group members.


## Distribuition

| Name    | Server | Client (tests)  |
|:-------:|:------:|:---------------:|
| Lívio   | rst    | pts             |
| Gabriel | hub    | rst             |
| Rafael  | pts    | hub             |


## Getting Started

The overall system is composed of multiple services and clients.
The main service is the _hub_ service that is aided by the _pts_ service.
There are also multiple _rst_ services, one for each participating restaurant.

See the project statement for a full description of the domain and the system.



### Prerequisites

Java Developer Kit 8 is required running on Linux, Windows or Mac.
Maven 3 is also required.

To confirm that you have them installed, open a terminal and type:

```
javac -version

mvn -version
```


### Configuring, Installing & Running the project

0. Compile and install all modules:

   ```
   mvn clean install -DskipTests
   ```

   (The tests are skipped because they require each server to be running)


#### If you want to run the tests:

1. Startup at least 1 Hub Server (hub-ws), 2 Restaurant Servers (rst-ws)
   and 1 Points Server (pts-ws). To do that, navigate to the respective
   folder an run:

   ```
   mvn compile exec:java
   ```

   **Warning**: To be able to run 2 Restaurants, you need to provide a `ws.i`
   different than the default one for one of the instances; to do that, run

   ```
   mvn exec:java -Dws.i=2
   ```

   The tests written for the Hub assume that all the servers where started with
   the default arguments, except for the 2nd Restaurant, that should be run with
   the above command.


2. In the root of the project, while the servers are up, run:

   ```
   mvn verify
   ```

   (The servers are officialy running when the line "`Press enter to shutdown`"
   appears in the screen)


   This command can take a while.


## Built With

* [Maven](https://maven.apache.org/) - Build Tool and Dependency Management
* [JAX-WS](https://javaee.github.io/metro-jax-ws/) - SOAP Web Services implementation for Java


## Versioning

We use [SemVer](http://semver.org/) for versioning. 

