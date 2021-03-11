package cloud.folium.demo.domain

import com.fasterxml.jackson.annotation.JsonProperty

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
		var country: String,

        @JsonProperty("NewConfirmed")
		var newConfirmed: Int,

        @JsonProperty("NewDeaths")
		var newDeaths: Int,

        @JsonProperty("NewRecovered")
		var newRecovered: Int,
	)
}
