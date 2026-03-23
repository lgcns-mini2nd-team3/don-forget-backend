package com.example.template_service.exception;

public class InvalidCategoryException extends RuntimeException {
    public InvalidCategoryException(String category) {
        super("유효하지 않은 category입니다: " + category);
    }
}