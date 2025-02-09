package com.app.expensetracker.securityAnnotation;

import com.app.expensetracker.shared.rest.enumeration.SecurityQueryEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityLayer {
    SecurityQueryEnum securityQueryEnum();

}
