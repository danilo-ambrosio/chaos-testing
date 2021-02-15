package com.skyharbor.aircraftmonitoring.model.flight;

import io.vlingo.common.Completes;

import io.vlingo.lattice.model.stateful.StatefulEntity;

public final class FlightEntity extends StatefulEntity<FlightState> implements Flight {
  private FlightState state;

  public FlightEntity(final String id) {
    super(String.valueOf(id));
    this.state = FlightState.identifiedBy(id);
  }

  public Completes<FlightState> departFromGate(final String aircraft, final String actualDeparture, final String estimatedArrival) {
    final FlightState stateArg = state.departFromGate(aircraft, actualDeparture, estimatedArrival);
    return apply(stateArg, new DepartedFromGate(stateArg), () -> state);
  }

  public Completes<FlightState> reportLocation(final String location) {
    final FlightState stateArg = state.reportLocation(location);
    return apply(stateArg, new LocationReported(stateArg), () -> state);
  }

  public Completes<FlightState> changeStatus(final String status) {
    final FlightState stateArg = state.changeStatus(status);
    return apply(stateArg, new Landed(stateArg), () -> state);
  }

  /*
   * Received when my current state has been applied and restored.
   *
   * @param state the FlightState
   */
  @Override
  protected void state(final FlightState state) {
    this.state = state;
  }

  /*
   * Received when I must provide my state type.
   *
   * @return {@code Class<FlightState>}
   */
  @Override
  protected Class<FlightState> stateType() {
    return FlightState.class;
  }
}
