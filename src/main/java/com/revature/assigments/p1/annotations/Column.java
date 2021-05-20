package com.revature.assigments.p1.annotations;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";
    String dataType() default "";
    String unique() default "false";
    String notNull() default "false";
    
}
