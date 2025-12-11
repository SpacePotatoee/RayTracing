package sp.sponge.scene.registries;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Registry<T> implements Iterable<T> {
    protected final List<T> registryList = new ArrayList<>();

    public abstract T register(T entry);

    public abstract T get(String name);

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return registryList.iterator();
    }

}
