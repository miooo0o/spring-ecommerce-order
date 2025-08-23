package ecommerce.exception

import ecommerce.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errors = e.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors)
    }

    @ExceptionHandler(
        value = [
            ConflictException::class,
            OrderStockConflictException::class,
        ],
    )
    fun handleConflict(e: ConflictException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(HttpStatus.CONFLICT, e.message)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(e: NotFoundException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Oops... The requested resource was not found on this server.")
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(e: UnauthorizedException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.message)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(e: ForbiddenException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(HttpStatus.FORBIDDEN, e.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(e: Exception): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.")
    }

    @ExceptionHandler(
        value = [
            RuntimeException::class,
            IllegalArgumentException::class,
            ProcessingException::class,
        ],
    )
    fun handleBadRequest(e: Exception): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.message, e.extractErrors())
    }

    private fun Exception.extractErrors(): List<String>? {
        return when (this) {
            is ProcessingException -> this.getDetailErrors()
            is IllegalArgumentException -> listOf("Invalid argument: ${this.message}")
            else -> null
        }
    }

    private fun buildErrorResponse(
        status: HttpStatus,
        message: String?,
        errors: List<String>? = null,
    ): ResponseEntity<ErrorResponse> {
        val response =
            ErrorResponse(
                status = status.value(),
                message = message,
                errors = errors,
            )
        return ResponseEntity.status(status).body(response)
    }
}
