package cloud.folium.demo.service

import cloud.folium.demo.domain.CovidData
import org.springframework.scheduling.annotation.Async
import java.util.concurrent.CompletableFuture

interface ICovidDataService {

    fun loadCovidData(): CompletableFuture<CovidData>
}
