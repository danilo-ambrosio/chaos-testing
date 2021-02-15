package com.skyharbor.aircraftmonitoring.infrastructure;

import com.skyharbor.aircraftmonitoring.infrastructure.persistence.CommandModelStateStoreProvider;
import com.skyharbor.aircraftmonitoring.infrastructure.persistence.ProjectionDispatcherProvider;
import com.skyharbor.aircraftmonitoring.infrastructure.persistence.QueryModelStateStoreProvider;
import com.skyharbor.aircraftmonitoring.infrastructure.resource.FlightResource;
import io.vlingo.actors.Grid;
import io.vlingo.cluster.model.Properties;
import io.vlingo.http.resource.Configuration.Sizing;
import io.vlingo.http.resource.Configuration.Timing;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry;

public class Bootstrap {
  private static Bootstrap instance;

  private final Grid grid;
  private final Server server;

  public Bootstrap(final String nodeName) throws Exception {
    grid = Grid.start("aircraft-monitoring", nodeName);
    final StatefulTypeRegistry registry = new StatefulTypeRegistry(grid.world());

    QueryModelStateStoreProvider.using(grid, registry);
    CommandModelStateStoreProvider.using(grid, registry, ProjectionDispatcherProvider.using(grid).storeDispatcher);

    final int serverPort = resolveServerPort(nodeName);
    server = Server.startWith(grid, Resources.are(new FlightResource(grid).routes()), serverPort, Sizing.define(), Timing.define());

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      if (instance != null) {
        instance.server.stop();

        System.out.println("\n");
        System.out.println("=========================");
        System.out.println("Stopping aircraft-monitoring.");
        System.out.println("=========================");
      }
    }));
  }

  private int resolveServerPort(final String nodeName) {
    final int port = 10000 + Properties.instance.applicationPort(nodeName);
    System.out.println(nodeName + " server running on " + port);
    return port;
  }

  public static void main(final String[] args) throws Exception {
    System.out.println("=========================");
    System.out.println("service: aircraft-monitoring.");
    System.out.println("=========================");

    instance = new Bootstrap(parseNodeName(args));
  }

  private static String parseNodeName(final String[] args) {
    if (args.length == 0) {
      System.err.println("The node must be named with a command-line argument.");
      System.exit(1);
    } else if (args.length > 1) {
      System.err.println("Too many arguments; provide node name only.");
      System.exit(1);
    }
    return args[0];
  }
}
