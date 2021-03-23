package cloud.folium.demo.integration.containers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile

class CovidTrackerContainerInfo(val endpoint: String, val container: CovidTrackerContainer)

class CovidTrackerContainer(dockerImageName: String) : GenericContainer<CovidTrackerContainer>(dockerImageName)

class CustomCovidTrackerImage() : ImageFromDockerfile() {
    fun build() = resolve()
}