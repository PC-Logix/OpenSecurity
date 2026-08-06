package pcl.opensecurity.blockentity;

import java.util.function.Consumer;

/**
 * Common-side bridge for alarm client ticking. The physical client installs the
 * real handler during mod construction; dedicated servers retain the no-op.
 */
public final class AlarmClientHooks {
    private static Consumer<AlarmBlockEntity> ticker = alarm -> {};

    public static void install(Consumer<AlarmBlockEntity> clientTicker) {
        ticker = clientTicker;
    }

    public static void tick(AlarmBlockEntity alarm) {
        ticker.accept(alarm);
    }

    private AlarmClientHooks() {}
}
