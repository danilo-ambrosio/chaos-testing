package com.skyharbor.aircraftmonitoring.infrastructure.persistence;

import java.util.Arrays;
import java.util.List;

import com.skyharbor.aircraftmonitoring.model.flight.FlightState;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.Stage;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry.Info;
import io.vlingo.symbio.StateAdapterProvider;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.xoom.actors.Settings;
import io.vlingo.xoom.storage.Model;
import io.vlingo.xoom.storage.StoreActorBuilder;
import io.vlingo.xoom.annotation.persistence.Persistence.StorageType;


public class CommandModelStateStoreProvider {
  private static CommandModelStateStoreProvider instance;

  public final StateStore store;

  public static CommandModelStateStoreProvider instance() {
    return instance;
  }

  public static CommandModelStateStoreProvider using(final Stage stage, final StatefulTypeRegistry registry) {
    return using(stage, registry, new NoOpDispatcher());
  }

  @SuppressWarnings("rawtypes")
  public static CommandModelStateStoreProvider using(final Stage stage, final StatefulTypeRegistry registry, final Dispatcher ...dispatchers) {
    if (instance != null) {
      return instance;
    }

    final StateAdapterProvider stateAdapterProvider = new StateAdapterProvider(stage.world());
    stateAdapterProvider.registerAdapter(FlightState.class, new FlightStateAdapter());

    new EntryAdapterProvider(stage.world()); // future use

    StateTypeStateStoreMap.stateTypeToStoreName(FlightState.class, FlightState.class.getSimpleName());

    final StateStore store =
            StoreActorBuilder.from(stage, Model.COMMAND, Arrays.asList(dispatchers), StorageType.STATE_STORE, Settings.properties(), true);

    registry.register(new Info(store, FlightState.class, FlightState.class.getSimpleName()));

    instance = new CommandModelStateStoreProvider(stage, store);

    return instance;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private CommandModelStateStoreProvider(final Stage stage, final StateStore store) {
    this.store = store;
  }
}
