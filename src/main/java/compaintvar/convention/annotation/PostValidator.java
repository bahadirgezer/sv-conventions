package compaintvar.convention.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PostValidator implements ConstraintValidator<ValidBody, String> {

    public boolean isValid(String post, ConstraintValidatorContext cxt) {
        return (!post.stripLeading().startsWith("Asla") && post.stripTrailing().endsWith("."));
    }

}
