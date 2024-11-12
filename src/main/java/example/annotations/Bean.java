package example.annotations;

import example.ScopeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

public @interface Bean {
    ScopeEnum scope() default ScopeEnum.SINGLETON;
}
