package zdoctor.mcskilltree.events;

import net.minecraftforge.eventbus.api.Event;

public class DataFixer extends Event {

    private boolean fixed;

    public void setFixed() {
        this.fixed = true;
    }

    public boolean isFixed() {
        return fixed;
    }

    public static class EnumPropertyFixer<T extends Enum<T>> extends DataFixer {

        private final String name;
        private final Class<? extends Enum<? extends Enum>> clazz;

        private Enum<T> value;

        public <T extends Enum<T>> EnumPropertyFixer(String name, Class<? extends Enum<T>> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public void setValue(Enum<T> value) {
            this.value = value;

            setFixed();
        }

        public Enum<T> getValue() {
            return value;
        }
    }
}
