package cloud.folium.demo.integration

import cloud.folium.demo.integration.containers.CovidTrackerContainer
import cloud.folium.demo.integration.containers.CovidTrackerContainerInfo
import cloud.folium.demo.integration.containers.CustomCovidTrackerImage
import cloud.folium.demo.integration.containers.MockServerContainerInfo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.Spec
import org.mockserver.client.server.MockServerClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.containers.Network
import org.testcontainers.shaded.org.apache.http.client.HttpClient
import org.testcontainers.shaded.org.apache.http.impl.client.HttpClientBuilder
import org.testcontainers.shaded.org.apache.http.impl.client.HttpClients
import java.io.File
import java.nio.file.Paths
import org.testcontainers.shaded.org.apache.http.client.config.RequestConfig




open class CovidTrackerIntegrationTestBase(spec: Spec.() -> Unit) : Spek(spec) {

    companion object {

        val logger: Logger = LoggerFactory.getLogger(this.javaClass)
        private val network = Network.newNetwork()
        private val mockServerContainerInfo: MockServerContainerInfo = startMockServerContainer()
        val mockServerClient: MockServerClient = mockServerContainerInfo.mockServerClient
        val covidTrackerContainerInfo: CovidTrackerContainerInfo = startCovidTrackerContainer()
        val client: HttpClient = initHttpClient()

        private fun initHttpClient(): HttpClient {
            val config = RequestConfig.custom()
                .setConnectTimeout(20_000)
                .setConnectionRequestTimeout(20_000)
                .setSocketTimeout(20_000)
                .build()
            return HttpClientBuilder.create().setDefaultRequestConfig(config).build()
        }

        private fun startMockServerContainer(): MockServerContainerInfo {

            logger.debug("Starting mock server container")

            val networkAlias = "covid_api"
            val mockServerContainer = MockServerContainer()
                .withNetwork(network)
                .withNetworkAliases(networkAlias)
                .withNetworkMode("host")
            mockServerContainer.start()

            logger.debug("Mockserver ip ${mockServerContainer.containerIpAddress} and port ${mockServerContainer.getMappedPort(80)}")

            return MockServerContainerInfo(
                endpoint = "http://${mockServerContainer.containerIpAddress}:${mockServerContainer.firstMappedPort}",
                mockServerClient = MockServerClient(mockServerContainer.containerIpAddress, mockServerContainer.serverPort),
                networkAlias = networkAlias
            )
        }

        private fun startCovidTrackerContainer(): CovidTrackerContainerInfo {

            val covidTrackerPath = findDockerfileForModule("demo")
            logger.debug("Starting covid tracker app with path for dockerfile $covidTrackerPath")

            val covidTrackerDockerImage = CustomCovidTrackerImage()
                .withFileFromFile("Dockerfile", File("$covidTrackerPath/Dockerfile"))
                .withFileFromFile(
                    "target/covid-tracker.jar",
                    File("$covidTrackerPath/target/covid-tracker.jar")
                )
            (covidTrackerDockerImage as CustomCovidTrackerImage).build()

            val covidTrackerContainer = CovidTrackerContainer(covidTrackerDockerImage.dockerImageName)
                .withNetwork(network)
                .withEnv("COVID_API", "http://${mockServerContainerInfo.networkAlias}")
            covidTrackerContainer.start()

            return CovidTrackerContainerInfo(
                endpoint = "http://${covidTrackerContainer.containerIpAddress}:${covidTrackerContainer.getMappedPort(8080)}",
                container = covidTrackerContainer
            )
        }
    }
}

fun findDockerfileForModule(moduleName: String): String = Paths.get("").toAbsolutePath().toString()