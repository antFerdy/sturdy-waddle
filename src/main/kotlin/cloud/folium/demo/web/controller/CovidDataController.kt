package cloud.folium.demo.web.controller

import cloud.folium.demo.domain.CovidData
import cloud.folium.demo.service.CovidDataService
import cloud.folium.demo.service.ICovidDataService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
class CovidDataController {

    @Autowired
    lateinit var covidService: ICovidDataService

    @GetMapping("/covid-statistics/stat-by-countries")
    fun getStatByCountries(): CompletableFuture<CovidData> = covidService.loadCovidData()
}
