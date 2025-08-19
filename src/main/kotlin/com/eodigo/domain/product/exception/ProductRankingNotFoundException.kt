package com.eodigo.domain.product.exception

import com.eodigo.common.exception.CustomException
import com.eodigo.common.exception.ErrorCode

class ProductRankingNotFoundException : CustomException(ErrorCode.PRODUCT_RANKING_NOT_FOUND)
