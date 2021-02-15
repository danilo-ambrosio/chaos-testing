package com.skyharbor.aircraftmonitoring.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.skyharbor.aircraftmonitoring.model.flight.FlightState;

public class FlightData {
  public final String id;
  public final String status;
  public final String aircraft;
  public final String actualDeparture;
  public final String estimatedArrival;
  public final String actualArrival;
  public final String location;

  public static FlightData from(final FlightState state) {
    return new FlightData(state);
  }

  public static List<FlightData> from(final List<FlightState> states) {
    return states.stream().map(FlightData::from).collect(Collectors.toList());
  }

  public static FlightData empty() {
    return new FlightData(FlightState.identifiedBy(null));
  }

  private FlightData (final FlightState state) {
    this.id = state.id;
    this.status = state.status;
    this.aircraft = state.aircraft;
    this.actualDeparture = state.actualDeparture;
    this.estimatedArrival = state.estimatedArrival;
    this.actualArrival = state.actualArrival;
    this.location = state.location;
  }

}
