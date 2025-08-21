package com.eodigo.common.initializer

import com.eodigo.domain.product.entity.Product
import com.eodigo.domain.product.enums.ProductSource
import com.eodigo.domain.product.repository.ProductRepository
import com.eodigo.external.kamis.KamisApiClient
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Profile("!test")
class ProductMasterInitializer(
    private val productRepository: ProductRepository,
    private val kamisApiClient: KamisApiClient,
    @Value("\${kamis.api.key}") private val certKey: String,
    @Value("\${kamis.api.cert-id}") private val certId: String,
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(args: ApplicationArguments?) {
        if (productRepository.existsBySource(ProductSource.KAMIS)) {
            log.info(
                "[ProductMasterInitializer] KAMIS product master data already exists. Skipping."
            )
            return
        }

        log.info("[ProductMasterInitializer] Initializing KAMIS product master data...")

        val kamisResponse = kamisApiClient.getProductInfo(certKey, certId)

        if (kamisResponse == null || kamisResponse.info.isEmpty()) {
            log.warn("[ProductMasterInitializer] No product info data found from KAMIS API.")
            return
        }

        val newProducts = kamisResponse.info.mapNotNull { it.toEntity() }
        productRepository.saveAll(newProducts)

        log.info(
            "[ProductMasterInitializer] Successfully initialized {} KAMIS products.",
            newProducts.size,
        )
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class KamisProductInfoResponse(val info: List<KamisProductInfoDto> = emptyList())

@JsonIgnoreProperties(ignoreUnknown = true)
data class KamisProductInfoDto(
    @JsonProperty("itemcategorycode") val categoryCode: String,
    @JsonProperty("itemcategoryname") val categoryName: String,
    @JsonProperty("itemcode") val itemCode: String,
    @JsonProperty("itemname") val itemName: String,
    @JsonProperty("kindcode") val kindCode: String?,
    @JsonProperty("kindname") val kindName: String?,
) {
    fun toEntity(): Product? {
        if (categoryCode.isBlank() || itemCode.isBlank()) return null

        val productName = if (!kindName.isNullOrBlank() && kindName != "없음") kindName else itemName

        return Product(
            name = productName.trim(),
            categoryCode = categoryCode.toInt(),
            categoryName = categoryName,
            itemCode = itemCode.toInt(),
            itemName = itemName,
            kindCode = kindCode?.toIntOrNull(),
            kindName = if (kindName.isNullOrBlank() || kindName == "없음") null else kindName,
            source = ProductSource.KAMIS,
        )
    }
}
