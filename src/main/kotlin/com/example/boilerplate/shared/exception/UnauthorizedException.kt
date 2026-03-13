package com.example.boilerplate.common.exception

class UnauthorizedException(message: String = "Unauthorized") :
        ApiException("UNAUTHORIZED", message)
