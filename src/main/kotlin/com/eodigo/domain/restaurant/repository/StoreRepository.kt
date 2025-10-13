package com.eodigo.domain.restaurant.repository

import com.eodigo.domain.restaurant.entity.Store
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StoreSearchResult {
    fun getStoreId(): Long
    fun getStoreName(): String
    fun getDistance(): Double
    fun getMenuName(): String
    fun getPrice(): Int
    fun getLatitude(): Double
    fun getLongitude(): Double
    fun getAddress(): String
    fun getImgUrl(): String?
}

interface StoreRepository : JpaRepository<Store, Long> {

    @Query(
            nativeQuery = true,
            value = """
            SELECT
                s.id as storeId,
                s.name as storeName,
                ST_Distance_Sphere(s.location, ST_SRID(POINT(:userLng, :userLat), 4326)) as distance,
                m.name as menuName,
                m.price as price,
                ST_X(s.location) as latitude,
                ST_Y(s.location) as longitude,
                s.address as address,
                s.img_url as imgUrl
            FROM
                store_backup s
            JOIN
                menu m ON s.id = m.store_id
            WHERE
                s.category = :category
                AND m.name LIKE CONCAT('%', :menuName, '%')
                AND MBRContains(
                    ST_GeomFromText(CONCAT(
                        'POLYGON((',
                        :southWestLat, ' ', :southWestLng, ',',
                        :northEastLat, ' ', :southWestLng, ',',
                        :northEastLat, ' ', :northEastLng, ',',
                        :southWestLat, ' ', :northEastLng, ',',
                        :southWestLat, ' ', :southWestLng,
                        '))'
                    ), 4326),
                    s.location
                )
        """
    )
    fun findStoresAndMenusInArea(
            @Param("userLng") userLng: Double,
            @Param("userLat") userLat: Double,
            @Param("category") category: String,
            @Param("menuName") menuName: String,
            @Param("southWestLng") southWestLng: Double,
            @Param("southWestLat") southWestLat: Double,
            @Param("northEastLng") northEastLng: Double,
            @Param("northEastLat") northEastLat: Double
    ): List<StoreSearchResult>
}