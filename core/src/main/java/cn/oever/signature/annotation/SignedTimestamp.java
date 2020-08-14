package cn.oever.signature.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TThe annotation indicates that the field is a timestamp
 */
@Target({ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SignedTimestamp {
}
