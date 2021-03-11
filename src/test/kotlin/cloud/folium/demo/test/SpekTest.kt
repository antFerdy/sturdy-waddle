package cloud.folium.demo.test

import cloud.folium.demo.domain.CovidData
import cloud.folium.demo.exception.CovidApiCallException
import cloud.folium.demo.service.CovidDataService
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import io.kotlintest.matchers.shouldBe
import io.kotlintest.mock.`when`
import io.netty.handler.codec.http.HttpHeaders
import org.asynchttpclient.*
import org.asynchttpclient.uri.Uri
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CompletableFuture


object SpekTest : Spek({

    describe("#load covid data") {

        val client = mock<AsyncHttpClient>()
        val objectMapper = mock<ObjectMapper>()
        val covidService = CovidDataService(client, objectMapper, uriForCovidApi)

        beforeEachTest {
            reset(client, objectMapper)
        }

        on("client returns correct json body") {
            val mockRequest = prepareRequest(uriForCovidApi, "GET")

            val brb = BoundRequestBuilder(client, mockRequest)
            `when`(client.prepareGet(any())).thenReturn(brb)

            val responseMock = prepareResponse(200, "OK", dummyJson)

            val future = mock<ListenableFuture<Any>>()
            `when`(future.toCompletableFuture()).thenReturn(
                CompletableFuture.completedFuture(responseMock)
            )

            `when`(
                client.executeRequest(any<Request>(), any<AsyncHandler<Any>>())
            ).thenReturn(future)

            val response = covidService.loadCovidData().get()

            it("assert serialization") {
                response shouldBe dummyCovidData
            }
        }

        on("client returns empty body") {

            val mockRequest = prepareRequest(uriForCovidApi, "GET")

            val brb = BoundRequestBuilder(client, mockRequest)
            `when`(client.prepareGet(any())).thenReturn(brb)

            val responseMock = prepareResponse(200, "OK", "")

            val future = mock<ListenableFuture<Any>>()
            `when`(future.toCompletableFuture()).thenReturn(
                CompletableFuture.completedFuture(responseMock)
            )

            `when`(
                client.executeRequest(any<Request>(), any<AsyncHandler<Any>>())
            ).thenReturn(future)

            val responseFuture = covidService.loadCovidData()

            it("throws exception") {

                assert(responseFuture.isCompletedExceptionally)

                assertThrows<CovidApiCallException>("Service returned empty body") {
                    responseFuture.get()
                }
            }
        }

        on("client returns not 200 status") {

            val mockRequest = prepareRequest(uriForCovidApi, "GET")

            val brb = BoundRequestBuilder(client, mockRequest)
            `when`(client.prepareGet(any())).thenReturn(brb)

            val responseMock = prepareResponse(503, "Service Unavailable Error", "")

            val future = mock<ListenableFuture<Any>>()
            `when`(future.toCompletableFuture()).thenReturn(
                CompletableFuture.completedFuture(responseMock)
            )

            `when`(
                client.executeRequest(any<Request>(), any<AsyncHandler<Any>>())
            ).thenReturn(future)

            val responseFuture = covidService.loadCovidData()

            it("throws exception") {

                assert(responseFuture.isCompletedExceptionally)

                assertThrows<CovidApiCallException>("Service returned error") {
                    responseFuture.get()
                }
            }
        }

    }
})

const val uriForCovidApi = "https://api.covid19api.com/summary"

private fun prepareResponse(statusCode: Int, statusText: String, responseBody: String): Response {

    val responseMock = mock<Response>()
    `when`(responseMock.statusCode).thenReturn(statusCode)
    `when`(responseMock.statusText).thenReturn(statusText)
    `when`(responseMock.responseBody).thenReturn(responseBody)

    return responseMock
}

private fun prepareRequest(uri: String, method: String): Request {

    val mockRequest = mock<Request>()
    `when`(mockRequest.headers).thenReturn(HttpHeaders.EMPTY_HEADERS)
    `when`(mockRequest.method).thenReturn(method)
    `when`(mockRequest.uri).thenReturn(Uri.create(uri))

    return mockRequest
}

private const val dummyJson = "{\"Global\":{\"NewConfirmed\":240179,\"NewDeaths\":7132,\"NewRecovered\":136631}," +
    "\"Countries\":[{\"NewConfirmed\":0,\"NewDeaths\":0,\"NewRecovered\":0,\"country\":\"Afghanistan\"}]}"

private val dummyCovidData = CovidData(
    global = CovidData.GlobalData(newConfirmed = 240179, newDeaths = 7132, newRecovered = 136631),
    countries = listOf(
        CovidData.CountryData(
            country = "Afghanistan",
            newConfirmed = 0,
            newRecovered = 0,
            newDeaths = 0
        )
    )
)
