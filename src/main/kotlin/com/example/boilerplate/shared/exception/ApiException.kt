package com.example.boilerplate.common.exception

open class ApiException(val code: String, message: String) : RuntimeException(message)
