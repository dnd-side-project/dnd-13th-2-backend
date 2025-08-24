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
        // 메뉴명과 카테고리로 필터링
        val menus =
            menuRepository.findByNameContaining(request.menuName).filter {
                it.store.category == request.category
            }

        // 가게별로 그룹화한 뒤, 각 가게에서 가장 저렴한 메뉴 하나만 선택
        val cheapestMenuByStore =
            menus
                .groupBy { it.store }
                .mapNotNull { (store, menusInStore) ->
                    val cheapestMenu = menusInStore.minByOrNull { it.price }
                    cheapestMenu?.let { store to it }
                }

        // 지도 경계값 내의 매장 필터링
        val locationFilteredMenus =
            cheapestMenuByStore.filter { (store, _) ->
                store.latitude >= request.southWestLat &&
                    store.latitude <= request.northEastLat &&
                    store.longitude >= request.southWestLng &&
                    store.longitude <= request.northEastLng
            }

        val storeDtoList =
            locationFilteredMenus.map { (store, menu) ->
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
                lat2 = store.latitude,
                lon2 = store.longitude,
            )

        return StoreDetailDto(
            storeId = storeId,
            name = store.name,
            distance = distance.toInt(),
            category = store.category,
            address = store.address,
            latitude = store.latitude,
            longitude = store.longitude,
            imgUrl = store.imgUrl,
            menus =
                menus.map { menu ->
                    val menuId = requireNotNull(menu.id)
                    MenuDto(id = menuId, name = menu.name, price = menu.price)
                },
        )
    }
}
