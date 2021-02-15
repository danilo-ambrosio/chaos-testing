package com.skyharbor.aircraftmonitoring.infrastructure.resource;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.DynamicResourceHandler;
import static io.vlingo.http.resource.ResourceBuilder.resource;

import com.skyharbor.aircraftmonitoring.infrastructure.persistence.FlightQueries;
import com.skyharbor.aircraftmonitoring.infrastructure.persistence.QueryModelStateStoreProvider;
import com.skyharbor.aircraftmonitoring.model.flight.FlightEntity;
import com.skyharbor.aircraftmonitoring.model.flight.Flight;
import com.skyharbor.aircraftmonitoring.infrastructure.FlightData;

import io.vlingo.http.Response;
import io.vlingo.common.Completes;

import java.io.Serializable;

import static io.vlingo.common.serialization.JsonSerialization.serialized;
import static io.vlingo.http.Response.Status.*;
import static io.vlingo.http.ResponseHeader.*;

public class FlightResource extends DynamicResourceHandler implements Serializable {
  private final FlightQueries $queries;

  public FlightResource(final Stage stage) {
      super(stage);
      this.$queries = QueryModelStateStoreProvider.instance().flightQueries;
  }

  public Completes<Response> reportLocation(final String id, final FlightData data) {
    return resolve(id)
            .andThenTo(flight -> flight.reportLocation(data.location))
            .andThenTo(state -> Completes.withSuccess(Response.of(Ok, serialized(FlightData.from(state)))))
            .otherwise(noGreeting -> Response.of(NotFound, location(id)))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  public Completes<Response> departFromGate(final FlightData data) {
    return Flight.departFromGate(stage(), data.aircraft, data.actualDeparture, data.estimatedArrival)
      .andThenTo(state -> Completes.withSuccess(Response.of(Created, headers(of(Location, location(state.id))), serialized(FlightData.from(state))))
      .otherwise(arg -> Response.of(NotFound, location()))
      .recoverFrom(e -> Response.of(InternalServerError, e.getMessage())));
  }

  public Completes<Response> flights() {
    return $queries.flights()
            .andThenTo(data -> Completes.withSuccess(Response.of(Ok, serialized(data))))
            .otherwise(arg -> Response.of(NotFound, location()))
            .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
  }

  @Override
  public Resource<?> routes() {
     return resource("FlightResource",
        io.vlingo.http.resource.ResourceBuilder.patch("/flights/{id}/location")
            .param(String.class)
            .body(FlightData.class)
            .handle(this::reportLocation),
        io.vlingo.http.resource.ResourceBuilder.post("/flights")
            .body(FlightData.class)
            .handle(this::departFromGate),
        io.vlingo.http.resource.ResourceBuilder.get("/flights")
            .handle(this::flights)
     );
  }

  private String location() {
    return location("");
  }

  private String location(final String id) {
    return "/flights/" + id;
  }

  private Completes<Flight> resolve(final String id) {
    return stage().actorOf(Flight.class, stage().addressFactory().from(id), Definition.has(FlightEntity.class, Definition.parameters(id)));
  }

}