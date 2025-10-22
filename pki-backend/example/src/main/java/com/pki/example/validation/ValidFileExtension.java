package com.pki.example.validation;



import javax.validation.Constraint;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileExtensionValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileExtension {
    String message() default "Invalid file extension";
    String[] extensions();
    Class<?>[] groups() default {};
    //Class<? extends Payload>[] payload() default {};
}
