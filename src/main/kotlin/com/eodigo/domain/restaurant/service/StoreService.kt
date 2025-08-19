package com.eodigo.domain.restaurant.service

import com.eodigo.domain.restaurant.dto.SearchRequest
import com.eodigo.domain.restaurant.dto.StoreDto
import com.eodigo.domain.restaurant.enums.SortType
import com.eodigo.domain.restaurant.repository.MenuRepository
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreService(private val menuRepository: MenuRepository) {
    @Transactional(readOnly = true)
    fun searchStores(request: SearchRequest): List<StoreDto> {
        // 메뉴명과 카테고리로 필터링
        val menus =
            menuRepository.findByNameContaining(request.menuName).filter {
                it.store.category == request.category
            }

        // 지도 경계값 내의 매장 필터링
        val locationFilteredMenus =
            menus.filter { menu ->
                val store = menu.store
                store.latitude >= request.southWestLat &&
                    store.latitude <= request.northEastLat &&
                    store.longitude >= request.southWestLng &&
                    store.longitude <= request.northEastLng
            }

        val storeDtoList =
            locationFilteredMenus.map { menu ->
                val store = menu.store
                val distance =
                    calculateDistance(
                        lat1 = request.userLat,
                        lon1 = request.userLng,
                        lat2 = store.latitude,
                        lon2 = store.longitude,
                    )
                val storeId = requireNotNull(store.id)
                StoreDto(
                    storeId = storeId,
                    storeName = store.name,
                    distance = distance.toInt(),
                    menuName = menu.name,
                    price = menu.price,
                    latitude = store.latitude,
                    longitude = store.longitude,
                    address = store.address,
                    imgUrl = store.imgUrl,
                )
            }

        // 정렬
        return when (request.sort) {
            SortType.PRICE -> storeDtoList.sortedBy { it.price }
            SortType.DISTANCE -> storeDtoList.sortedBy { it.distance }
        }
    }

    // Haversine 공식을 사용한 거리 계산 (단위: 미터)
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000 // 지구 반지름
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a =
            sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) *
                    cos(Math.toRadians(lat2)) *
                    sin(dLon / 2) *
                    sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
