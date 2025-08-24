package com.eodigo.common.exception

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    /** @Valid 어노테이션을 통한 DTO 검증 실패 시 발생하는 예외를 처리 */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    private fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException
    ): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.INVALID_INPUT_VALUE
        val summarizedErrors =
            e.bindingResult.fieldErrors.joinToString(", ") { "${it.field}:${it.defaultMessage}" }
        log.warn(
            "MethodArgumentNotValidException: code={}, message='{}'",
            errorCode.code,
            summarizedErrors,
        )
        val fieldError = e.bindingResult.fieldErrors.firstOrNull()
        val errorMessage = fieldError?.defaultMessage ?: errorCode.message
        val response = ErrorResponse.of(errorCode, errorMessage)
        return ResponseEntity(response, errorCode.status)
    }

    /** @RequestParam으로 지정된 필수 파라미터 누락 시 발생하는 예외를 처리 */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    private fun handleMissingServletRequestParameterException(
        e: MissingServletRequestParameterException
    ): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.INVALID_INPUT_VALUE
        val message = "필수 파라미터 '${e.parameterName}'(이)가 누락되었습니다."
        log.warn(
            "MissingServletRequestParameterException: code={}, message='{}'",
            errorCode.code,
            e.message,
        )
        val response = ErrorResponse.of(errorCode, message)
        return ResponseEntity(response, errorCode.status)
    }

    /** @PathVariable 등에서 타입 변환 실패 시 발생하는 예외를 처리 */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    private fun handleMethodArgumentTypeMismatchException(
        e: MethodArgumentTypeMismatchException
    ): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.INVALID_TYPE_VALUE
        log.warn(
            "MethodArgumentTypeMismatchException: code={}, message='{}'",
            errorCode.code,
            e.message,
        )
        val response = ErrorResponse.of(errorCode)
        return ResponseEntity(response, errorCode.status)
    }

    /** 지원하지 않는 HTTP 메서드 요청 시 발생하는 예외를 처리 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    private fun handleHttpRequestMethodNotSupportedException(
        e: HttpRequestMethodNotSupportedException
    ): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.METHOD_NOT_ALLOWED
        log.warn(
            "HttpRequestMethodNotSupportedException: code={}, message='{}'",
            errorCode.code,
            e.message,
        )
        val response = ErrorResponse.of(errorCode)
        return ResponseEntity(response, errorCode.status)
    }

    /** 존재하지 않는 URL 요청 시 발생하는 예외를 처리 */
    @ExceptionHandler(NoResourceFoundException::class)
    private fun handleNoResourceFoundException(
        e: NoResourceFoundException
    ): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.URL_NOT_FOUND
        log.warn(
            "NoResourceFoundException: code={}, message='{}', path='{}'",
            errorCode.code,
            errorCode.message,
            e.resourcePath,
        )
        val response = ErrorResponse.of(errorCode)
        return ResponseEntity(response, errorCode.status)
    }

    /** 직접 정의한 비즈니스 예외를 처리 */
    @ExceptionHandler(CustomException::class)
    private fun handleCustomException(e: CustomException): ResponseEntity<ErrorResponse> {
        val errorCode = e.errorCode
        log.warn("Custom Exception: code={}, message='{}'", errorCode.code, e.message)
        val response = ErrorResponse.of(errorCode)
        return ResponseEntity(response, errorCode.status)
    }

    /** 위에서 처리되지 못한 모든 예외를 처리 */
    @ExceptionHandler(Exception::class)
    private fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled Exception", e)
        val response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR)
        return ResponseEntity(response, ErrorCode.INTERNAL_SERVER_ERROR.status)
    }
}
