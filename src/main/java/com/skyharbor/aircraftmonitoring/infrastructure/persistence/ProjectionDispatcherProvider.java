package com.skyharbor.aircraftmonitoring.infrastructure.persistence;

import java.util.Arrays;
import java.util.List;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.Stage;
import io.vlingo.lattice.model.projection.ProjectionDispatcher;
import io.vlingo.lattice.model.projection.ProjectionDispatcher.ProjectToDescription;
import io.vlingo.lattice.model.projection.TextProjectionDispatcherActor;
import io.vlingo.symbio.store.dispatch.Dispatcher;

import com.skyharbor.aircraftmonitoring.model.flight.Landed;
import com.skyharbor.aircraftmonitoring.model.flight.DepartedFromGate;
import com.skyharbor.aircraftmonitoring.model.flight.InFlight;
import com.skyharbor.aircraftmonitoring.model.flight.LocationReported;
import com.skyharbor.aircraftmonitoring.model.flight.ArrivedAtGate;

@SuppressWarnings("rawtypes")
public class ProjectionDispatcherProvider {
  private static ProjectionDispatcherProvider instance;

  public final ProjectionDispatcher projectionDispatcher;
  public final Dispatcher storeDispatcher;

  public static ProjectionDispatcherProvider instance() {
    return instance;
  }

  public static ProjectionDispatcherProvider using(final Stage stage) {
    if (instance != null) return instance;

    final List<ProjectToDescription> descriptions =
            Arrays.asList(
                    ProjectToDescription.with(FlightProjectionActor.class, Landed.class.getName(), LocationReported.class.getName(), DepartedFromGate.class.getName(), InFlight.class.getName(), ArrivedAtGate.class.getName())
                    );

    final Protocols dispatcherProtocols =
            stage.actorFor(
                    new Class<?>[] { Dispatcher.class, ProjectionDispatcher.class },
                    Definition.has(TextProjectionDispatcherActor.class, Definition.parameters(descriptions)));

    final Protocols.Two<Dispatcher, ProjectionDispatcher> dispatchers = Protocols.two(dispatcherProtocols);

    instance = new ProjectionDispatcherProvider(dispatchers._1, dispatchers._2);

    return instance;
  }

  private ProjectionDispatcherProvider(final Dispatcher storeDispatcher, final ProjectionDispatcher projectionDispatcher) {
    this.storeDispatcher = storeDispatcher;
    this.projectionDispatcher = projectionDispatcher;
  }
}
