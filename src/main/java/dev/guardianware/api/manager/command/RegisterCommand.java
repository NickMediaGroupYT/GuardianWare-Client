package dev.guardianware.api.manager.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface RegisterCommand {
    String name();

    String syntax();

    String[] aliases();

    String description() default "No description.";
}
