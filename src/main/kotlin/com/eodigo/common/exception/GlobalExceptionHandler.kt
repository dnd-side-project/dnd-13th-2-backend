package com.eodigo.common.exception

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    /** @Valid 어노테이션을 통한 DTO 검증 실패 시 발생하는 예외를 처리 */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    private fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponse> {
        log.warn("handleMethodArgumentNotValidException: {}", e.bindingResult.fieldErrors)
        val fieldError = e.bindingResult.fieldErrors.firstOrNull()
        val errorMessage = fieldError?.defaultMessage ?: ErrorCode.INVALID_INPUT_VALUE.message
        val response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, errorMessage)
        return ResponseEntity(response, ErrorCode.INVALID_INPUT_VALUE.status)
    }

    /** @PathVariable 등에서 타입 변환 실패 시 발생하는 예외를 처리 */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    private fun handleMethodArgumentTypeMismatchException(
        e: MethodArgumentTypeMismatchException
    ): ResponseEntity<ErrorResponse> {
        log.warn("handleMethodArgumentTypeMismatchException: {}", e.message)
        val response = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE)
        return ResponseEntity(response, ErrorCode.INVALID_TYPE_VALUE.status)
    }

    /** 지원하지 않는 HTTP 메서드 요청 시 발생하는 예외를 처리 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    private fun handleHttpRequestMethodNotSupportedException(
        e: HttpRequestMethodNotSupportedException
    ): ResponseEntity<ErrorResponse> {
        log.warn("handleHttpRequestMethodNotSupportedException: {}", e.message)
        val response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED)
        return ResponseEntity(response, ErrorCode.METHOD_NOT_ALLOWED.status)
    }

    /** 직접 정의한 비즈니스 예외를 처리 */
    @ExceptionHandler(CustomException::class)
    private fun handleCustomException(e: CustomException): ResponseEntity<ErrorResponse> {
        log.error("handleCustomException: {}", e.errorCode, e)
        val errorCode = e.errorCode
        val response = ErrorResponse.of(errorCode)
        return ResponseEntity(response, errorCode.status)
    }

    /** 위에서 처리되지 못한 모든 예외를 처리 */
    @ExceptionHandler(Exception::class)
    private fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("handleException: {}", e.message, e)
        val response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR)
        return ResponseEntity(response, ErrorCode.INTERNAL_SERVER_ERROR.status)
    }
}
