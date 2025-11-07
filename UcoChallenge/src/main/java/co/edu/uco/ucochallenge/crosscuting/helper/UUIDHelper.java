package co.edu.uco.ucochallenge.crosscuting.helper;

import java.util.UUID;

public final class UUIDHelper {

    private static final UUID DEFAULT_UUID = new UUID(0L, 0L);

    private UUIDHelper() {
        super();
    }

    public static UUID getDefault(final UUID value) {
        return value == null ? DEFAULT_UUID : value;
    }

    public static boolean isDefault(final UUID value) {
        return value == null || DEFAULT_UUID.equals(value);
    }

    public static UUID getNewUUID() {
        return UUID.randomUUID();
    }
}
