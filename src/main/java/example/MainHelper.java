package example;

import example.annotations.Controller;
import example.server.Server;
import example.tester.Test;

import java.io.IOException;
import java.util.Set;

public class MainHelper {


    private final DiEngine diEngine = DiEngine.getInstance();
    private final RouteMapper routeMapper = new RouteMapper();

    public  void run() {
        Test test = diEngine.inject(Test.class);
        Test test2 = diEngine.inject(Test.class);
        test.log();
        test2.log();
        test2.log();

        try {
            Set<Class<?>> controllers = ClassFinder.findAllClassesWithAnnotation(Controller.class);
            for (Class<?> controllerClazz : controllers) {
                createAndRegisterRoutes(controllerClazz);
            }

            Server.run(routeMapper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private <T> void createAndRegisterRoutes(Class<T> controllerClazz) {
        T obj = diEngine.inject(controllerClazz);
        routeMapper.registerRoutes(controllerClazz, obj);
    }
}
