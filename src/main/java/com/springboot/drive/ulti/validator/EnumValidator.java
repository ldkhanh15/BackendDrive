package com.springboot.drive.ulti.validator;

import com.springboot.drive.ulti.anotation.ValidEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {

    private ValidEnum annotation;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // Consider null invalid, adjust as necessary
        }

        // Check if value is valid enum constant
        for (Enum<?> enumConstant : annotation.enumClass().getEnumConstants()) {
            if (enumConstant.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
