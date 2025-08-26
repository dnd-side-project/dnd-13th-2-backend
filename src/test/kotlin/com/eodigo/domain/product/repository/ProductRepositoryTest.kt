package com.eodigo.domain.product.repository

import com.eodigo.domain.product.entity.Product
import com.eodigo.domain.product.enums.ProductSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {
    @Autowired private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun setUp() {
        productRepository.deleteAll()

        val products =
            listOf(
                Product(
                    name = "대추방울토마토",
                    categoryCode = "200",
                    categoryName = "채소",
                    itemCode = "210",
                    itemName = "토마토",
                    source = ProductSource.KAMIS,
                    kindCode = null,
                    kindName = null,
                ),
                Product(
                    name = "토마토",
                    categoryCode = "200",
                    categoryName = "채소",
                    itemCode = "210",
                    itemName = "토마토",
                    source = ProductSource.KAMIS,
                    kindCode = null,
                    kindName = null,
                ),
                Product(
                    name = "배추",
                    categoryCode = "200",
                    categoryName = "채소",
                    itemCode = "211",
                    itemName = "배추",
                    source = ProductSource.KAMIS,
                    kindCode = null,
                    kindName = null,
                ),
            )
        productRepository.saveAll(products)
    }

    @Test
    @DisplayName("findByNameContaining: '토마토'가 포함된 상품 2개를 반환한다")
    fun findByNameContaining_Success_WhenKeywordMatches() {
        // given
        val keyword = "토마토"

        // when
        val results = productRepository.findByNameContaining(keyword)

        // then
        assertThat(results).hasSize(2)
        assertThat(results).extracting("name").containsExactlyInAnyOrder("대추방울토마토", "토마토")
    }

    @Test
    @DisplayName("findByNameContaining: '방울'이 포함된 상품 1개를 반환한다")
    fun findByNameContaining_Success_WhenKeywordPartiallyMatches() {
        // given
        val keyword = "방울"

        // when
        val results = productRepository.findByNameContaining(keyword)

        // then
        assertThat(results).hasSize(1)
        assertThat(results.first().name).isEqualTo("대추방울토마토")
    }

    @Test
    @DisplayName("findByNameContaining: 일치하는 상품이 없으면 빈 리스트를 반환한다")
    fun findByNameContaining_ReturnsEmptyList_WhenKeywordDoesNotMatch() {
        // given
        val keyword = "없는상품"

        // when
        val results = productRepository.findByNameContaining(keyword)

        // then
        assertThat(results).isEmpty()
    }
}
