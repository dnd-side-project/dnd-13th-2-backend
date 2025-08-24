package com.eodigo.domain.restaurant.controller

import com.eodigo.common.exception.ErrorResponse
import com.eodigo.domain.restaurant.dto.SearchRequest
import com.eodigo.domain.restaurant.dto.StoreDetailDto
import com.eodigo.domain.restaurant.dto.StoreDto
import com.eodigo.domain.restaurant.service.StoreService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "외식 API", description = "외식 정보 관련 API")
@RestController
@RequestMapping("/api/v1/stores")
@Validated
class StoreController(private val storeService: StoreService) {

    @Operation(
        summary = "위치 기반 메뉴 최저가 매장 리스트 검색",
        description = "메뉴명, 위치, 지도 경계 등을 기반으로 최저가 매장 리스트를 검색합니다.",
    )
    @ApiResponses(
        value = [ApiResponse(responseCode = "200", description = "해당 메뉴 최저가 매장 리스트 조회 성공")]
    )
    @GetMapping("/search")
    fun searchStores(
        @Valid @ModelAttribute request: SearchRequest
    ): ResponseEntity<List<StoreDto>> {
        val stores = storeService.searchStores(request)
        return ResponseEntity.ok(stores)
    }

    @Operation(summary = "매장 상세 정보 조회", description = "해당 ID의 매장의 상세 정보를 조회합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "200", description = "해당 ID의 매장 상세 정보 조회 성공"),
                ApiResponse(
                    responseCode = "404",
                    description = "해당 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Store Not Found",
                                            description = "존재하지 않는 매장 ID",
                                            value =
                                                "{\"status\": 404, \"code\": \"S001\", \"message\": \"해당 매장을 찾을 수 없습니다.\"}",
                                        )
                                    ],
                            )
                        ],
                ),
            ]
    )
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
