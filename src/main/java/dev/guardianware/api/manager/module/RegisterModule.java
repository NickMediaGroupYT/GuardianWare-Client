package dev.guardianware.api.manager.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface RegisterModule {
    String name();

    Module.Category category();

    String tag() default " ";

    String description() default "No description.";

    boolean persistent() default false;

    int bind() default 0;
}