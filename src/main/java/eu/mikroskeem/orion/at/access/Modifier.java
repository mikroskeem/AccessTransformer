package eu.mikroskeem.orion.at.access;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents field/method modifiers
 *
 * @author Mark Vainomaa
 */
public enum Modifier {
    FINAL("f", Opcodes.ACC_FINAL);

    /** Access level name */
    private final String name;

    /** Access level opcode */
    private final int opcode;

    /**
     * {@link Modifier} enum constructor
     *
     * @param name Modifier name
     * @param opcode Modifier opcode, see {@link Opcodes}
     */
    Modifier(String name, int opcode) {
        this.name = name;
        this.opcode = opcode;
    }

    /**
     * Gets modifier name
     *
     * @return Modifier name
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Gets modifier opcode
     *
     * @return Modifier opcode
     */
    public int getOpcode() {
        return opcode;
    }

    /** Static list of opcodes mapped to enum keys */
    public final static Map<Integer, Modifier> BY_OPCODE;

    /** Static list of opcode names mapped to enum keys */
    public final static Map<String, Modifier> BY_NAME;

    static {
        //noinspection MismatchedQueryAndUpdateOfCollection
        Map<Integer, Modifier> byOpcode;
        //noinspection MismatchedQueryAndUpdateOfCollection
        Map<String, Modifier> byName;
        BY_OPCODE = Collections.unmodifiableMap((byOpcode = new HashMap<>(Modifier.values().length)));
        BY_NAME = Collections.unmodifiableMap((byName  = new HashMap<>(Modifier.values().length)));

        for (Modifier value : Modifier.values()) {
            byOpcode.put(value.opcode, value);
            byName.put(value.name, value);
        }
    }

    /**
     * AT Modifier entry
     */
    public static class ModifierEntry {
        /** If given modifier must be removed or added */
        private final boolean remove;

        /** Target modifier */
        private final Modifier modifier;

        /**
         * ModifierEntry constructor
         *
         * @param remove Whether modifier must be added or removed
         * @param modifier {@link Modifier}
         */
        public ModifierEntry(boolean remove, @NonNull Modifier modifier) {
            this.remove = remove;
            this.modifier = Objects.requireNonNull(modifier, "modifier must be not null");
        }

        /**
         * Returns whether given modifier should be added or removed
         *
         * @return Whether given modifier should be added or removed
         */
        public boolean isRemove() {
            return remove;
        }

        /**
         * Returns {@link Modifier} what should be applied to given element (class, method, field)
         *
         * @return {@link Modifier} instance
         */
        @NonNull
        public Modifier getModifier() {
            return modifier;
        }
    }
}
