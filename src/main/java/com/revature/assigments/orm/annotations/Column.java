package com.revature.assigments.orm.annotations;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";
    String dataType() default "";
    String unique() default "";
    String notNull() default "";
    
}
