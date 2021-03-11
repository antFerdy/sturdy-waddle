package cloud.folium.demo.web.controller

import cloud.folium.demo.domain.CovidData
import cloud.folium.demo.service.CovidDataService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
class CovidDataController(private val covidDataService: CovidDataService) {


    @GetMapping("/covid-statistics/stat-by-countries")
    fun getStatByCountries(): CompletableFuture<CovidData> = covidDataService.loadCovidData()
}
