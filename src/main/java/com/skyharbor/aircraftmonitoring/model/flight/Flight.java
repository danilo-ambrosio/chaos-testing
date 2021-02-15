package com.skyharbor.aircraftmonitoring.model.flight;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Address;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;

public interface Flight {

  Completes<FlightState> departFromGate(final String aircraft, final String actualDeparture, final String estimatedArrival);

  static Completes<FlightState> departFromGate(final Stage stage, final String aircraft, final String actualDeparture, final String estimatedArrival) {
    final Address _address = stage.addressFactory().uniquePrefixedWith("g-");
    final Flight _flight = stage.actorFor(Flight.class, Definition.has(FlightEntity.class, Definition.parameters(_address.idString())), _address);
    return _flight.departFromGate(aircraft, actualDeparture, estimatedArrival);
  }

  Completes<FlightState> reportLocation(final String location);

  Completes<FlightState> changeStatus(final String status);

}