package com.suapp.dcdownloader.retrofit.network;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by wangzhen@jiandaola.com on 23/12/2016.
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface NoEncrypt {
}
