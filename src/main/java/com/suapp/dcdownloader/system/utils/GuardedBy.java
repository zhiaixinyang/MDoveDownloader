package com.suapp.dcdownloader.system.utils;

/**
 * Created by zhaojing on 2018/3/19.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Annotation type used to mark a method or field that can only be accessed when
 * holding the referenced lock.
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.CLASS)
public @interface GuardedBy {
    String value();
}
