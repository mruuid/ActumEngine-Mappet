package unkn0wn.ae.utils;

import mchorse.metamorph.api.morphs.AbstractMorph;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side storage for first-person morphs.
 *
 * <p>The first-person morph (set via {@code setMorph(morph, true)}) only lives
 * on the client inside {@link RenderingHandler#clientMorph}, but scripts run on
 * the server and cannot read client-only state. This map keeps a copy of the
 * last first-person morph assigned to each player so {@code getMorph(true)} and
 * {@code clearMorph()} can work from server-side script contexts.</p>
 */
public class ServerMorphStorage {
    private ServerMorphStorage() {
    }

    private static final Map<UUID, AbstractMorph> FIRST_PERSON = new HashMap<>();

    public static void setFirstPerson(UUID id, AbstractMorph morph) {
        if (morph == null) {
            FIRST_PERSON.remove(id);
        } else {
            FIRST_PERSON.put(id, morph);
        }
    }

    public static AbstractMorph getFirstPerson(UUID id) {
        return id == null ? null : FIRST_PERSON.get(id);
    }

    public static void clearFirstPerson(UUID id) {
        if (id != null) {
            FIRST_PERSON.remove(id);
        }
    }
}
