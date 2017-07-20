package com.airhacks.doit.business;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CrossCheckConstraintValidator implements ConstraintValidator<CrossCheck, ValidEntity> {
    
    @Override
    public void initialize(CrossCheck constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(ValidEntity value, ConstraintValidatorContext context) {
        return value.isValid();
    }
}
