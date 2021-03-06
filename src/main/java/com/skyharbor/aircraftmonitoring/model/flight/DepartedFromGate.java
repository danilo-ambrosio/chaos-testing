package com.skyharbor.aircraftmonitoring.model.flight;

import java.util.UUID;
import io.vlingo.common.version.SemanticVersion;
import io.vlingo.lattice.model.IdentifiedDomainEvent;

public final class DepartedFromGate extends IdentifiedDomainEvent {

  private final UUID eventId;
  public final String id;
  public final String aircraft;
  public final String actualDeparture;

  public DepartedFromGate(final FlightState state) {
    super(SemanticVersion.from("0.0.1").toValue());
    this.id = state.id;
    this.aircraft = state.aircraft;
    this.actualDeparture = state.actualDeparture;
    this.eventId = UUID.randomUUID(); //TODO: Define the event id
  }

  @Override
  public String identity() {
    return eventId.toString();
  }
}
