package com.eodigo.batch.scheduler

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class KamisJobScheduler(
    private val jobLauncher: JobLauncher,
    @Qualifier("kamisDailyPriceSyncJob") private val dailyPriceSyncJob: Job,
    @Qualifier("kamisAnnualPriceSyncJob") private val annualPriceSyncJob: Job,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    // 일별 가격 동기화 스케줄
    // 초 분 시 일 월 요일 (매주 화~토요일 새벽 3시 0분에 실행)
    @Scheduled(cron = "0 0 3 ? * TUE-SAT", zone = "Asia/Seoul")
    fun runDailyPriceSyncJob() {
        log.info("Starting kamisDailyPriceSyncJob by scheduler...")
        try {
            val surveyDate =
                LocalDate.now(ZoneId.of("Asia/Seoul"))
                    .minusDays(1)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE)
            val jobParameters =
                JobParametersBuilder()
                    .addLocalDateTime("run.time", LocalDateTime.now())
                    .addString("surveyDate", surveyDate)
                    .toJobParameters()
            jobLauncher.run(dailyPriceSyncJob, jobParameters)
        } catch (e: Exception) {
            log.error("Failed to start kamisDailyPriceSyncJob by scheduler", e)
        }
    }

    // 연간 가격 동기화 스케줄
    // 매주 일요일 새벽 4시 0분에 실행
    @Scheduled(cron = "0 0 4 ? * SUN", zone = "Asia/Seoul")
    fun runAnnualPriceSyncJob() {
        log.info("Starting kamisAnnualPriceSyncJob by scheduler...")
        try {
            val jobParameters =
                JobParametersBuilder()
                    .addLocalDateTime("run.time", LocalDateTime.now())
                    .toJobParameters()
            jobLauncher.run(annualPriceSyncJob, jobParameters)
        } catch (e: Exception) {
            log.error("Failed to start kamisAnnualPriceSyncJob by scheduler", e)
        }
    }
}
