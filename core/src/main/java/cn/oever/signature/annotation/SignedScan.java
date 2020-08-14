package cn.oever.signature.annotation;


import org.springframework.context.annotation.Import;
import cn.oever.signature.service.BaseSignedService;
import cn.oever.signature.util.RedisUtil;

import java.lang.annotation.*;

/**
 * The annotation is in the Application class and is used to scan other implementation classes
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({BaseSignedService.class, RedisUtil.class})
public @interface SignedScan {
}
