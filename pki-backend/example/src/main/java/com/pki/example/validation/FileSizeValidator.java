package com.pki.example.validation;


import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class FileSizeValidator implements ConstraintValidator<ValidFileSize, MultipartFile> {

    private long maxSizeInBytes;

    @Override
    public void initialize(ValidFileSize constraintAnnotation) {
        this.maxSizeInBytes = constraintAnnotation.maxSizeInBytes();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Let @NotNull handle null validation
        }

        return file.getSize() <= maxSizeInBytes;
    }
}
