package ecommerce.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleConstraintViolations(e: MethodArgumentNotValidException): ResponseEntity<String> {
        val errors = e.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflictException(e: ConflictException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.message)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(e: NotFoundException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Oops... The requested resource was not found on this server.")
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(e: UnauthorizedException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.message)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleInternalError(e: ForbiddenException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An unexpected error occurred.")
    }
}
