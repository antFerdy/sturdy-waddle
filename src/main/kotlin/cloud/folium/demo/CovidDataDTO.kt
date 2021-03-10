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

        @JsonProperty("Country")
		var Country: String,

        @JsonProperty("Date")
		var Date: Date,

        @JsonProperty("NewConfirmed")
		var newConfirmed: Int,

        @JsonProperty("NewDeaths")
		var newDeaths: Int,

        @JsonProperty("NewRecovered")
		var newRecovered: Int,

        )
}