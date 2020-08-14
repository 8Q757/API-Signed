package cn.oever.signature.annotation;


import java.lang.annotation.*;

/**
 * The annotation indicates that this is an entity class used as a request
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SignedEntity {
}
