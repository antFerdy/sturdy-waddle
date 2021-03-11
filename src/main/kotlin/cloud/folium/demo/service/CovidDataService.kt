package cloud.folium.demo.service

import cloud.folium.demo.domain.CovidData
import cloud.folium.demo.exception.CovidApiCallException
import com.fasterxml.jackson.databind.ObjectMapper
import org.asynchttpclient.AsyncHttpClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import java.util.concurrent.CompletableFuture

class CovidDataService(private val client: AsyncHttpClient, private val objectMapper: ObjectMapper) : ICovidDataService {

    @Value("\${covidApi.uri}")
    lateinit var covidApiUri: String

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Async
    override fun loadCovidData(): CompletableFuture<CovidData> {
        logger.debug("Covid api call on $covidApiUri")
        return client
            .prepareGet(covidApiUri)
            .execute()
            .toCompletableFuture()
            .thenApply {
                if (it.statusCode != 200) {
                    logger.error("Service returned error ${it.responseBody}")
                    throw CovidApiCallException(
                        httpStatusCode = it.statusCode.toString(),
                        response = it.responseBody,
                        msg = "Service calling error: ${it.statusCode}")
                }

                if (it.responseBody.isNullOrEmpty()) {
                    logger.error("Service returned empty body")
                    throw CovidApiCallException(
                        httpStatusCode = it.statusCode.toString(),
                        response = it.responseBody,
                        msg = "Service returned empty body")
                }

                logger.debug("Response body from third party api ${it.responseBody}")
                objectMapper.readValue(it.responseBody, CovidData::class.java)
            }
    }
}
