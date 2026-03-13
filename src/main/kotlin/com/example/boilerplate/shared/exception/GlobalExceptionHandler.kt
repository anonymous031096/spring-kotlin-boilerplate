package com.example.boilerplate.shared.exception

import com.example.boilerplate.common.exception.BusinessException
import com.example.boilerplate.common.exception.ForbiddenException
import com.example.boilerplate.common.exception.NotFoundException
import com.example.boilerplate.common.exception.UnauthorizedException
import com.example.boilerplate.common.exception.ValidationException
import com.example.boilerplate.shared.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidError(ex: MethodArgumentNotValidException): ApiResponse<Any> {

        val errors =
                ex.bindingResult.fieldErrors.associate {
                    it.field to (it.defaultMessage ?: "invalid")
                }

        return ApiResponse<Any>(success = false, message = "Validation error", data = errors)
    }

    @ExceptionHandler(ValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationError(ex: ValidationException): ApiResponse<Any> {

        return ApiResponse<Any>(success = false, message = "Validation error", data = ex.errors)
    }

    @ExceptionHandler(BusinessException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBusiness(ex: BusinessException): ApiResponse<Any> {

        return ApiResponse<Any>(success = false, message = ex.message ?: "Business error")
    }

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: NotFoundException): ApiResponse<Any> {

        return ApiResponse<Any>(success = false, message = ex.message ?: "Not found")
    }

    @ExceptionHandler(UnauthorizedException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleUnauthorized(ex: UnauthorizedException): ApiResponse<Any> {

        return ApiResponse<Any>(success = false, message = ex.message ?: "Unauthorized")
    }

    @ExceptionHandler(ForbiddenException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbidden(ex: ForbiddenException): ApiResponse<Any> {

        return ApiResponse<Any>(success = false, message = ex.message ?: "Forbidden")
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAuthorizationDenied(ex: AuthorizationDeniedException): ApiResponse<Any> {
        return ApiResponse<Any>(success = false, message = ex.message ?: "Access denied")
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUnknownError(ex: Exception): ApiResponse<Nothing> {
        val requestId = MDC.get("requestId")

        log.error("Unhandled exception requestId=$requestId", ex)

        return ApiResponse(success = false, message = "Internal error. requestId=$requestId")
    }
}
