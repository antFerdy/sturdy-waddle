package cloud.folium.demo

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.asynchttpclient.AsyncHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class CovidService(var client: AsyncHttpClient) {

	private final val hostName: String = "https://api.covid19api.com"

	private final val mapper: ObjectMapper = ObjectMapper()

	init {

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
	}

	fun loadCovidData(): CompletableFuture<CovidData> {
		return client
			.prepareGet("$hostName/summary")
			.execute()
			.toCompletableFuture()
			.thenApply {
				if(it.statusCode != 200 || it.responseBody.isNullOrEmpty()) {
					throw RuntimeException("Service calling error: ${it.statusCode}")
				}

				mapper.readValue(it.responseBody)
			}
	}
}