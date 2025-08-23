package com.eodigo.batch.controller

import com.eodigo.common.exception.InvalidInputValueException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "배치 API", description = "배치 잡 수동 실행용 API")
@RestController
@RequestMapping("/api/v1/batch")
@Profile("local", "dev")
class BatchJobController(
    private val jobLauncher: JobLauncher,
    private val applicationContext: ApplicationContext,
) {

    @Operation(summary = "배치 잡 수동 실행", description = "지정된 이름의 배치 잡을 즉시 실행합니다.")
    @PostMapping("/run/{jobName}")
    fun runBatchJob(
        @Parameter(description = "실행할 Job의 Bean 이름", example = "kamisDailyPriceSyncJob")
        @PathVariable
        jobName: String,
        @Parameter(description = "kamisDailyPriceSyncJob으로 조회할 날짜", example = "2025-08-22")
        @RequestParam("surveyDate", required = false)
        surveyDate: String?,
    ): ResponseEntity<String> {
        return try {
            val job = applicationContext.getBean(jobName, Job::class.java)

            val jobParametersBuilder =
                JobParametersBuilder().addLocalDateTime("run.time", LocalDateTime.now())

            if (jobName == "kamisDailyPriceSyncJob") {
                val finalSurveyDate =
                    when {
                        surveyDate.isNullOrEmpty() ->
                            LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)

                        !isIsoLocalDate(surveyDate) -> throw InvalidInputValueException()
                        else -> surveyDate
                    }
                jobParametersBuilder.addString("surveyDate", finalSurveyDate)
            }

            val jobParameters = jobParametersBuilder.toJobParameters()

            jobLauncher.run(job, jobParameters)

            ResponseEntity.ok("Batch job '$jobName' has been started.")
        } catch (e: InvalidInputValueException) {
            throw InvalidInputValueException()
        } catch (e: Exception) {
            ResponseEntity.internalServerError()
                .body("Failed to start batch job '$jobName'. Reason: ${e.message}")
        }
    }

    fun isIsoLocalDate(dateString: String): Boolean {
        return try {
            LocalDate.parse(dateString)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }
}
