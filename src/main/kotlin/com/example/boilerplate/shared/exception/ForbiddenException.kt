package com.example.boilerplate.common.exception

class ForbiddenException(message: String = "Forbidden") : ApiException("FORBIDDEN", message)
