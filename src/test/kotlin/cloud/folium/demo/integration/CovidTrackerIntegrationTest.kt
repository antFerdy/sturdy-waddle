package cloud.folium.demo.integration

import io.kotlintest.matchers.shouldBe
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse
import org.testcontainers.shaded.org.apache.http.client.methods.HttpGet
import org.testcontainers.shaded.org.apache.http.impl.client.HttpClients


class CovidTrackerIntegrationTest : CovidTrackerIntegrationTestBase({

    describe("getting covid statistics") {

        on("container starts") {
            val covidTrackerContainer = covidTrackerContainerInfo.container
            logger.info("Container status ${covidTrackerContainer.isRunning}")

            it("they works correctly") {
                covidTrackerContainer.isRunning shouldBe true
            }
        }

        on("health check request") {
            val healthCheckEndpoint = "${covidTrackerContainerInfo.endpoint}/actuator/health"
            logger.debug("Health check on $healthCheckEndpoint")
            Thread.sleep(10_000) //looks like java process doesn't start immediately after container starts

            val client = HttpClients.createDefault()
            val getRequest = HttpGet(healthCheckEndpoint)
            val response = client.execute(getRequest)


            it("returns ok status") {
                response.statusLine.statusCode shouldBe 200
            }
        }

        on("covid tracker returns data") {

            val dummyJson = "{\"Global\":{\"NewConfirmed\":240179,\"NewDeaths\":7132,\"NewRecovered\":136631}," +
                "\"Countries\":[{\"NewConfirmed\":0,\"NewDeaths\":0,\"NewRecovered\":0,\"Country\":\"Afghanistan\"}]}"

            mockServerClient
                .`when`(
                    request().withMethod("GET")
                ).respond(
                    HttpResponse.response().withStatusCode(200).withBody(dummyJson)
                )

            val client = HttpClients.createDefault()
            val endpoint = "${covidTrackerContainerInfo.endpoint}/covid-statistics/stat-by-countries"
            logger.debug("Test request on $endpoint")

            val getRequest = HttpGet(endpoint)
            val response = client.execute(getRequest)

            it("returns 200 status code") {
                response.statusLine.statusCode shouldBe 200
            }
        }
    }

}) {
    
}

