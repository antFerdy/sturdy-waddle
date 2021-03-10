package cloud.folium.demo

import cloud.folium.demo.CovidData
import cloud.folium.demo.CovidService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
class CovidRestController {

	@Autowired
	lateinit var covidService: CovidService

	@GetMapping("/covid/stat")
	fun getStat(): CovidData {
		return covidService.loadCovidData().join()
	}

	@GetMapping("/covid/stat/v2")
	fun getStatV2(): CompletableFuture<CovidData> {
		return covidService.loadCovidData()
	}




}