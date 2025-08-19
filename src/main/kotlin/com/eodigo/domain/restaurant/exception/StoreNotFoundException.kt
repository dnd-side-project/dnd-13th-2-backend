package com.eodigo.domain.restaurant.exception

import com.eodigo.common.exception.CustomException
import com.eodigo.common.exception.ErrorCode

class StoreNotFoundException : CustomException(ErrorCode.STORE_NOT_FOUND)
