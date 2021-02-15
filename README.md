# Aircraft Monitoring

This is a simple demo app created for chaos testing purposes. Based on VLINGO/CLUSTER, it supports up to three nodes and achieves a quorum when, at least, two healthy nodes are running.

# Execution

First, build the application using Maven:

```
mvn clean package
```

Then, run the three nodes separately passing the node name as the program argument.

```
 $ ./java -jar target/aircraft-monitoring-1.0.0.jar node1
 $ ./java -jar target/aircraft-monitoring-1.0.0.jar node2
 $ ./java -jar target/aircraft-monitoring-1.0.0.jar node3
```

As showed above, the expected node names are `node1, node2, node3`.
