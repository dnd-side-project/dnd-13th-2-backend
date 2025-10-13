package com.eodigo.domain.restaurant.service

import com.eodigo.domain.restaurant.dto.MenuDto
import com.eodigo.domain.restaurant.dto.SearchRequest
import com.eodigo.domain.restaurant.dto.StoreDetailDto
import com.eodigo.domain.restaurant.dto.StoreDto
import com.eodigo.domain.restaurant.enums.SortType
import com.eodigo.domain.restaurant.exception.StoreNotFoundException
import com.eodigo.domain.restaurant.repository.MenuRepository
import com.eodigo.domain.restaurant.repository.StoreRepository
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreService(
    private val menuRepository: MenuRepository,
    private val storeRepository: StoreRepository,
) {
    @Transactional(readOnly = true)
    fun searchStores(request: SearchRequest): List<StoreDto> {
        // DB에서 공간 검색 및 최저가 메뉴 필터링을 모두 수행
        val searchResults =
            storeRepository.findStoresAndMenusInArea(
                menuName = request.menuName,
                category = request.category.name,
                userLat = request.userLat,
                userLng = request.userLng,
                southWestLat = request.southWestLat,
                southWestLng = request.southWestLng,
                northEastLat = request.northEastLat,
                northEastLng = request.northEastLng,
            )

        val cheapestStoresPerStore =
            searchResults
                .groupBy { it.getStoreId() } // 가게 ID로 그룹화
                .mapNotNull { (_, results) -> // 각 가게에 대해
                    results.minByOrNull { it.getPrice() } // 가격이 가장 낮은 메뉴
                }

        val storeDtoList =
            cheapestStoresPerStore.map { result ->
                StoreDto(
                    storeId = result.getStoreId(),
                    storeName = result.getStoreName(),
                    distance = result.getDistance().toInt(),
                    menuName = result.getMenuName(),
                    price = result.getPrice(),
                    latitude = result.getLatitude(),
                    longitude = result.getLongitude(),
                    address = result.getAddress(),
                    imgUrl = result.getImgUrl(),
                )
            }

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

    @Transactional(readOnly = true)
    fun getStoreDetails(storeId: Long, latitude: Double, longitude: Double): StoreDetailDto {
        // ID로 매장 정보 조회
        val store = storeRepository.findByIdOrNull(storeId) ?: throw StoreNotFoundException()

        // 해당 매장의 모든 메뉴 조회
        val menus = menuRepository.findAllByStoreId(storeId)

        val distance =
            calculateDistance(
                lat1 = latitude,
                lon1 = longitude,
                lat2 = store.location.x,
                lon2 = store.location.y,
            )

        return StoreDetailDto(
            storeId = storeId,
            name = store.name,
            distance = distance.toInt(),
            category = store.category,
            address = store.address,
            latitude = store.location.y,
            longitude = store.location.x,
            imgUrl = store.imgUrl,
            menus =
                menus.map { menu ->
                    val menuId = requireNotNull(menu.id)
                    MenuDto(id = menuId, name = menu.name, price = menu.price)
                },
        )
    }
}
