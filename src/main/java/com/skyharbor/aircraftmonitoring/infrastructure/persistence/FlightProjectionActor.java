package com.skyharbor.aircraftmonitoring.infrastructure.persistence;

import com.skyharbor.aircraftmonitoring.infrastructure.Events;
import com.skyharbor.aircraftmonitoring.infrastructure.FlightData;

import com.skyharbor.aircraftmonitoring.model.flight.FlightState;
import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.symbio.Source;

public class FlightProjectionActor extends StateStoreProjectionActor<FlightData> {
  private static final FlightData Empty = FlightData.empty();

  public FlightProjectionActor() {
    super(QueryModelStateStoreProvider.instance().store);
  }

  @Override
  protected FlightData currentDataFor(final Projectable projectable) {
    final FlightState flightState = projectable.object();
    return FlightData.from(flightState);
  }

  @Override
  protected FlightData merge(final FlightData previousData,
                             final int previousVersion,
                             final FlightData currentData,
                             final int currentVersion) {
    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case Landed:
        case LocationReported:
        case DepartedFromGate:
        case InFlight:
        case ArrivedAtGate:
          return currentData;
        default:
          logger().warn("Event of type " + event.typeName() + " was not matched.");
          break;
      }
    }

    return previousData;
  }
}
