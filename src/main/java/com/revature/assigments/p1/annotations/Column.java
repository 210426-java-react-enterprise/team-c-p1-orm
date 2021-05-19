package com.revature.assigments.p1.annotations;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";
    String dataType() default "";
    String constraint_NN() default "";
    String constraint_U() default "";
    
}
