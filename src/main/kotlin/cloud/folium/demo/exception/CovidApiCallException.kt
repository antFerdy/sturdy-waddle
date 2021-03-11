package cloud.folium.demo.exception

class CovidApiCallException(val httpStatusCode: String, val response: String, val msg: String ) : Exception(msg)