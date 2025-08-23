package com.eodigo.batch.writer

import com.eodigo.domain.product.entity.AnnualNationalPrice
import com.eodigo.domain.product.repository.AnnualNationalPriceRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter

class KamisAnnualPriceJpaItemWriter(
    private val annualNationalPriceRepository: AnnualNationalPriceRepository
) : ItemWriter<List<AnnualNationalPrice>> {

    override fun write(chunk: Chunk<out List<AnnualNationalPrice>>) {
        // 이중 리스트를 단일 리스트로 펼친다.
        val flattenedList = chunk.items.flatten()

        val itemsToSave = mutableListOf<AnnualNationalPrice>()

        for (item in flattenedList) {
            val productId =
                requireNotNull(item.product.id) {
                    "KamisAnnualPriceJpaItemWriter: product.id가 null입니다."
                }
            val existingPrice =
                annualNationalPriceRepository.findByProductIdAndSurveyYearAndMarketType(
                    productId,
                    item.surveyYear,
                    item.marketType,
                )

            if (existingPrice != null) {
                existingPrice.updatePrice(item.price)
                itemsToSave.add(existingPrice)
            } else {
                itemsToSave.add(item)
            }
        }

        if (itemsToSave.isNotEmpty()) {
            annualNationalPriceRepository.saveAll(itemsToSave)
        }
    }
}
