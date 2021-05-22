package com.revature.p1.utils.annotations;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name() default "";
    boolean nullable() default true;
    boolean isTimestamp() default false;
    int length() default 50; //will maybe use this
}
