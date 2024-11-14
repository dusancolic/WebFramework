package example;

import example.annotations.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DiEngine {
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final DependencyContainer dependencyContainer = DependencyContainer.getInstance();
    private static DiEngine instance;

    private DiEngine() {}

    public static DiEngine getInstance() {
        if (instance == null) {
            instance = new DiEngine();
        }
        return instance;
    }

    public <T> T inject(Class<T> clazz) {
        return injectField(clazz);
    }


    private <T> T injectField(Class<T> clazz) {
        try {
            T instance = clazz.getConstructor().newInstance();
            for (Field field: clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Autowired.class))
                    continue;
                field.setAccessible(true);

                Class<?> fieldType = getInjectableIfQualifier(field);

                if (isSingleton(fieldType)) {
                    Object singletonInstance = getSingleton(fieldType);
                    field.set(instance, singletonInstance);
                    log(field,singletonInstance);
                }
                else if (isPrototype(fieldType)) {
                    Object componentInstance = inject(fieldType);
                    field.set(instance, componentInstance); // field je npr logger, a instance je test, znaci da mi ubacujemo MyLogger implementaciju za logger u test
                    log(field,componentInstance);
                }
                else {
                    field.setAccessible(false);
                    throw new InstantiationException("No Dependency Injection for type: " + fieldType);
                }

                field.setAccessible(false);
            }
            return instance;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Class<?> getInjectableIfQualifier(Field field) {
        if(!field.isAnnotationPresent(Qualifier.class))
            return field.getType();   // ako nema Qualifier anotaciju, onda je tip polja tip klase

        Qualifier qualifier = field.getAnnotation(Qualifier.class);
        Class<?> type = field.getType();
        if (!type.isAssignableFrom(qualifier.value()))
            throw new RuntimeException("Qualifier type is not assignable to field type");

        return dependencyContainer.getInjection(field, qualifier.value());
    }

    private Object getSingleton(Class<?> type) {

        if (!singletons.containsKey(type)) {
            singletons.put(type, inject(type));
        }

        return singletons.get(type);
    }

    private boolean isSingleton(Class<?> clazz) {
        return clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Bean.class) && clazz.getAnnotation(Bean.class).scope() == ScopeEnum.SINGLETON;
    }

    private boolean isPrototype(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class) ||
                clazz.isAnnotationPresent(Bean.class) && clazz.getAnnotation(Bean.class).scope() == ScopeEnum.PROTOTYPE;
    }

    private void log(Field field, Object instance) {
        Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
        if (!autowiredAnnotation.verbose()) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        String objectType = field.getType().getSimpleName();
        String fieldName = field.getName();
        String parentClassName = field.getDeclaringClass().getSimpleName();
        int hashCode = instance.hashCode();
        LocalDateTime now = LocalDateTime.now();

        stringBuilder.append("Initialized ")
                .append(objectType).append(" ")
                .append(fieldName).append(" in ")
                .append(parentClassName).append(" on ")
                .append(now).append(" with ")
                .append(hashCode);

        System.out.println(stringBuilder);
    }
}
