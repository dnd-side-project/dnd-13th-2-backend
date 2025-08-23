package com.eodigo.batch.controller

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/batch")
@Profile("local", "dev")
class BatchJobController(
    private val jobLauncher: JobLauncher,
    private val applicationContext: ApplicationContext,
) {

    @PostMapping("/run/{jobName}")
    fun runBatchJob(@PathVariable jobName: String): ResponseEntity<String> {
        return try {
            val job = applicationContext.getBean(jobName, Job::class.java)

            val jobParametersBuilder =
                JobParametersBuilder().addLocalDateTime("run.time", LocalDateTime.now())

            if (jobName == "kamisDailyPriceSyncJob") {
                val surveyDate =
                    LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
                jobParametersBuilder.addString("surveyDate", surveyDate)
            }

            val jobParameters = jobParametersBuilder.toJobParameters()

            jobLauncher.run(job, jobParameters)

            ResponseEntity.ok("Batch job '$jobName' has been started.")
        } catch (e: Exception) {
            ResponseEntity.internalServerError()
                .body("Failed to start batch job '$jobName'. Reason: ${e.message}")
        }
    }
}
