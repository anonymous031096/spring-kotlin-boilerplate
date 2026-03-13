package com.example.boilerplate.common.exception

class NotFoundException(message: String) : ApiException("NOT_FOUND", message)
