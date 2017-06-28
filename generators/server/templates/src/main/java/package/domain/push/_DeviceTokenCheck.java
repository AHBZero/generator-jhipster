package <%=packageName%>.domain.push;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DeviceTokenValidator.class)
@Documented
public @interface DeviceTokenCheck {

    String message() default "Invalid Token";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
