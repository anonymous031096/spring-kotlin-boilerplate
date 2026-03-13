package com.example.boilerplate.common.exception

class BusinessException(message: String) : ApiException("BUSINESS_ERROR", message)
