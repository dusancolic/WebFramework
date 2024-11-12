package example.tester;


import example.annotations.Service;

@Service  // ovo mogu da promenim na @Component ili @Bean(scope = ScopeEnum.SINGLETON) radi testiranja
//@Component
//@Bean(scope = ScopeEnum.SINGLETON)
public class MyLogger implements Logger{
    int counter = 0;

    @Override
    public void log(String message) {
        System.out.println("MyLogger: " + message + " " + ++counter);
    }
}
