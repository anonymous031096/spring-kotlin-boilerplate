package com.example.boilerplate.common.exception

class ValidationException(val errors: Map<String, String>) :
        ApiException("VALIDATION_ERROR", "Validation failed")
