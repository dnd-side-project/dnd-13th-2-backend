package com.eodigo.domain.restaurant.controller

import com.eodigo.domain.restaurant.dto.SearchRequest
import com.eodigo.domain.restaurant.dto.StoreDetailDto
import com.eodigo.domain.restaurant.dto.StoreDto
import com.eodigo.domain.restaurant.service.StoreService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/stores")
@Validated
class StoreController(private val storeService: StoreService) {

    /** 메뉴명, 위치, 지도 경계 등을 기반으로 매장 리스트를 검색 */
    @GetMapping("/search")
    fun searchStores(
        @Valid @ModelAttribute request: SearchRequest
    ): ResponseEntity<List<StoreDto>> {
        val stores = storeService.searchStores(request)
        return ResponseEntity.ok(stores)
    }

    /** 매장 상세 정보 조회 */
    @GetMapping("/{storeId}")
    fun getStoreDetails(
        @Valid @PathVariable storeId: Long,
        latitude: Double,
        longitude: Double,
    ): ResponseEntity<StoreDetailDto> {
        val storeDetails = storeService.getStoreDetails(storeId, latitude, longitude)
        return ResponseEntity.ok(storeDetails)
    }
}
