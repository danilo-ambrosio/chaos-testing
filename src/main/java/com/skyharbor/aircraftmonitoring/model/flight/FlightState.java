package com.skyharbor.aircraftmonitoring.model.flight;

import io.vlingo.symbio.store.object.StateObject;

public final class FlightState extends StateObject {

  public final String id;
  public final String status;
  public final String aircraft;
  public final String actualDeparture;
  public final String estimatedArrival;
  public final String actualArrival;
  public final String location;

  public static FlightState identifiedBy(final String id) {
    return new FlightState(id, null, null, null, null, null, null);
  }

  public FlightState (final String id, final String status, final String aircraft, final String actualDeparture, final String estimatedArrival, final String actualArrival, final String location) {
    this.id = id;
    this.status = status;
    this.aircraft = aircraft;
    this.actualDeparture = actualDeparture;
    this.estimatedArrival = estimatedArrival;
    this.actualArrival = actualArrival;
    this.location = location;
  }

  public FlightState departFromGate(final String aircraft, final String actualDeparture, final String estimatedArrival) {
    return new FlightState(this.id, this.status, aircraft, actualDeparture, estimatedArrival, this.actualArrival, this.location);
  }

  public FlightState reportLocation(final String location) {
    return new FlightState(this.id, this.status, this.aircraft, this.actualDeparture, this.estimatedArrival, this.actualArrival, location);
  }

  public FlightState changeStatus(final String status) {
    return new FlightState(this.id, status, this.aircraft, this.actualDeparture, this.estimatedArrival, this.actualArrival, this.location);
  }

}
