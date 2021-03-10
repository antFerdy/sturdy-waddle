package cloud.folium.demo

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import io.kotlintest.matchers.shouldBe
import io.kotlintest.mock.`when`
import io.kotlintest.mock.spy
import io.netty.handler.codec.http.EmptyHttpHeaders
import kotlinx.coroutines.runBlocking
import org.asynchttpclient.*
import org.asynchttpclient.uri.Uri
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.assertThrows
import org.mockito.invocation.InvocationOnMock
import java.lang.RuntimeException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.function.Consumer


object SpekTest: Spek({
    val uriForCovid = "https://api.covid19api.com/summary"


    describe("#load covid data") {

        val client = mock<AsyncHttpClient>()
        val covidService = CovidService(client)


        on("client returns empty body") {


            val mockRequest = mock<Request>()
            `when`(mockRequest.headers).thenReturn(EmptyHttpHeaders.INSTANCE)
            `when`(mockRequest.method).thenReturn("GET")
            `when`(mockRequest.uri).thenReturn(Uri.create(uriForCovid))

            val brb = BoundRequestBuilder(client, mockRequest)
            `when`(client.prepareGet(any())).thenReturn(brb)


            // mock response
            val responseMock = mock<Response>()
            `when`(responseMock.statusCode).thenReturn(200)
            `when`(responseMock.statusText).thenReturn("Ok")
            `when`(responseMock.responseBody).thenReturn("")

            val future = mock<ListenableFuture<Any>>()
            `when`(future.toCompletableFuture()).thenReturn(
                CompletableFuture.completedFuture(responseMock)
            )

            `when`(
                client.executeRequest(any<Request>(), any<AsyncHandler<Any>>())
            ).thenReturn(future)


//            val response = runBlocking {  covidService.loadCovidData() }

            it("throws exception") {
                assertThrows<RuntimeException> { covidService.loadCovidData() }
            }

        }






    }
})