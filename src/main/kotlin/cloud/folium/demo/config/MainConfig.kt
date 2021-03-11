package cloud.folium.demo.config

import cloud.folium.demo.service.CovidDataService
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.Dsl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor


@Configuration
class MainConfig {

    @Value("\${covidApi.uri}")
    lateinit var covidApiUri: String

	@Bean
	fun asyncClient(): AsyncHttpClient {

		val builder = Dsl.config()
			.setConnectTimeout(1_000)
			.setRequestTimeout(3_000)
			.setReadTimeout(2_000)

		return Dsl.asyncHttpClient(builder)
	}

	@Bean
	fun objectMapper(): ObjectMapper = ObjectMapper()
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

	@Bean("taskExecutor")
	fun taskExecutor(): ThreadPoolTaskExecutor = ThreadPoolTaskExecutor().apply {
		corePoolSize = 2
		maxPoolSize = 2
		setQueueCapacity(100)
		setThreadNamePrefix("http-client")
		initialize()
	}

	@Bean
	fun covidDataService(): CovidDataService = CovidDataService(asyncClient(), objectMapper(), covidApiUri)
}
