package cloud.folium.demo

import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.Dsl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor




@Configuration
class Configs {


	@Bean
	fun configClient(): AsyncHttpClient {

		val builder = Dsl.config()
			.setConnectTimeout(1_000)
			.setRequestTimeout(3_000)

		return Dsl.asyncHttpClient(builder)
	}


	@Bean(name = ["taskExecutor"])
	fun taskExecutor(): ThreadPoolTaskExecutor? {

		val executor = ThreadPoolTaskExecutor()
		executor.corePoolSize = 2
		executor.maxPoolSize = 2
		executor.setQueueCapacity(100)
		executor.setThreadNamePrefix("http-client")
		executor.initialize()

		return executor
	}
}