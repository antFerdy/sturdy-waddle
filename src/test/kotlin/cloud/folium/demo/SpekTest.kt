package cloud.folium.demo

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.mock.`when`
import io.netty.handler.codec.http.EmptyHttpHeaders
import io.netty.handler.codec.http.HttpHeaders
import kotlinx.coroutines.runBlocking
import org.asynchttpclient.*
import org.asynchttpclient.uri.Uri
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.assertThrows
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.CompletableFuture



object SpekTest: Spek({
    val uriForCovid = "https://api.covid19api.com/summary"


    describe("#load covid data") {

        val client = mock<AsyncHttpClient>()
        val covidService = CovidService(client)

        beforeEachTest {
            reset(client)
        }

        on("client returns correct json body") {
            val mockRequest = prepareRequest(uriForCovid, "GET")

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

            val response = runBlocking {  covidService.loadCovidData().get() }

            it("assert serialization") {
                response shouldBe dummyCovidData
            }
        }

        on("client returns empty body") {

            val mockRequest = prepareRequest(uriForCovid, "GET")

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

            it("throws exception") {
                assertThrows<RuntimeException>("Service calling error") {
                    runBlocking {  covidService.loadCovidData().get() }
                }
            }
        }
    }
})

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
