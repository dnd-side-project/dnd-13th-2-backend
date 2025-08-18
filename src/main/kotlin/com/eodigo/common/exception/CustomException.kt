package com.eodigo.common.exception

/**
 * 프로젝트 전역에서 사용할 커스텀 예외의 최상위 클래스
 *
 * @property errorCode ErrorCode Enum
 */
open class CustomException(val errorCode: ErrorCode) : RuntimeException(errorCode.message)
