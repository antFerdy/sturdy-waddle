package cloud.folium.demo

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class CovidData(

	@JsonProperty("Global")
	val global: GlobalData,

	@JsonProperty("Countries")
	val countries: List<CountryData>
) {

	data class GlobalData(

		@JsonProperty("NewConfirmed")
		var newConfirmed: Int,

		@JsonProperty("NewDeaths")
		var newDeaths: Int,

		@JsonProperty("NewRecovered")
		var newRecovered: Int,
	)

	data class CountryData(
		@JsonProperty("country")
		var country: String,

        @JsonProperty("NewConfirmed")
		var newConfirmed: Int,

        @JsonProperty("NewDeaths")
		var newDeaths: Int,

        @JsonProperty("NewRecovered")
		var newRecovered: Int,

        )
}