package cloud.folium.demo.integration.containers

import org.mockserver.client.server.MockServerClient

class MockServerContainerInfo(
    val endpoint: String,
    val mockServerClient: MockServerClient,
    val networkAlias: String)