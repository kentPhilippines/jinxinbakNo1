package com.ruoyi.web.controller.tool;

import com.ruoyi.common.exception.BusinessException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Set;

public class PropertyValidateUtil {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> constraintViolationSet = validator.validate(object, Default.class);
        if(constraintViolationSet.size() == 0) {
            return;
        }
        StringBuffer message = new StringBuffer("");
        for (ConstraintViolation<T> constraintViolation : constraintViolationSet) {
            String prefix = message.toString().equals("") ? "" : ", ";
            message.append(prefix).append(constraintViolation.getPropertyPath().toString()).append(" ").append(constraintViolation.getMessage());
        }
        throw new BusinessException( message.toString());
    }

    public static <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> constraintViolationSet = validator.validate(object, groups);
        if(constraintViolationSet.size() == 0) {
            return;
        }
        StringBuffer message = new StringBuffer("");
        for (ConstraintViolation<T> constraintViolation : constraintViolationSet) {
            String prefix = message.toString().equals("") ? "" : ", ";
            message.append(prefix).append(constraintViolation.getPropertyPath().toString()).append(" ").append(constraintViolation.getMessage());
        }
        throw new BusinessException( message.toString());
    }
}