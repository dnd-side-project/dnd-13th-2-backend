package com.eodigo.domain.product.exception

import com.eodigo.common.exception.CustomException
import com.eodigo.common.exception.ErrorCode

class ProductTrendNotFoundException : CustomException(ErrorCode.PRODUCT_TREND_NOT_FOUND)
