package example;

import example.annotations.Qualifier;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {
    private static DependencyContainer instance;
    private final Map<String, Class<?>> implementations = new HashMap<>();
    private DependencyContainer() {}

    public static DependencyContainer getInstance() {
        if (instance == null) {
            instance = new DependencyContainer();
        }
        return instance;
    }

    private <T> void addInjection(Class<T> clazz, Class<? extends T> clazzImplementation, Class<?> qualifier) {

        implementations.put(generateKey(clazz,qualifier), clazzImplementation.asSubclass(clazz));
    }

    public <T> Class<? extends T> getInjection(Field field, Class<?> qualifier) {
        Class<T> type = (Class<T>) field.getType();
        String key = generateKey(type, qualifier);
        if (!implementations.containsKey(key)) {
            createInjection(field);
        }
        Class<?> injection = implementations.get(key);

        if (injection == null)
            throw new RuntimeException("No Dependency Injection for type: " + type);

        return injection.asSubclass(type);
    }

    private <T> void createInjection(Field field) {

        Qualifier qualifier = field.getAnnotation(Qualifier.class);
        Class<T> type = (Class<T>) field.getType();

        addInjection(type, qualifier.value().asSubclass(type),qualifier.value());
    }

    private String generateKey(Class<?> clazz, Class<?> qualifier) {
        return clazz.getName() + ":" + qualifier.getName();
    }
}
