package example;

import example.annotations.Controller;
import example.annotations.GET;
import example.annotations.POST;
import example.annotations.Path;
import example.framework.request.enums.MethodEnum;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RouteMapper {
    private final Map<String,Map<Object,Method>> routes = new HashMap<>();

    public <T> void registerRoutes(Class<T> controllerClazz, T object) {
        if (!controllerClazz.isAnnotationPresent(Controller.class) || !controllerClazz.isAnnotationPresent(Path.class))
            return;

        Path path = controllerClazz.getAnnotation(Path.class);


        for (Method method : controllerClazz.getDeclaredMethods()) {
            boolean isGet = method.isAnnotationPresent(GET.class);
            boolean isPost = method.isAnnotationPresent(POST.class);

            if (!isGet && !isPost)
                continue;

            if (isGet && isPost)
                throw new RuntimeException("Method can't be GET and POST at the same time in " + controllerClazz);

            StringBuilder stringBuilder = new StringBuilder();

            if (isGet)
                stringBuilder.append(MethodEnum.GET).append(" ");
            else
                stringBuilder.append(MethodEnum.POST).append(" ");

            stringBuilder.append(path.value());

            if (method.isAnnotationPresent(Path.class)) {
                Path methodPath = method.getAnnotation(Path.class);
                stringBuilder.append(methodPath.value());
            }
            Map<Object, Method> map = new HashMap<>();
            map.put(object, method);
            routes.put(stringBuilder.toString(), map);
        }
    }


    public void sendRequest(String url)  {
        if (url.equals("GET /favicon.ico"))
            return;

        if(routes.get(url) == null) {
            throw new RuntimeException("No route found for " + url);
        }

        Map<Object, Method> map = routes.get(url);
        Method method = map.get(map.keySet().toArray()[0]);
        method.setAccessible(true);
        try {
            method.invoke(map.keySet().toArray()[0]);
            method.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
