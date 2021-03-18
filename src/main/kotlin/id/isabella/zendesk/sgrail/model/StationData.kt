package id.isabella.zendesk.sgrail.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class StationData() {
    @JsonAlias("Station Code")
    lateinit var stationCode: String

    @JsonAlias("Station Name")
    lateinit var stationName: String

    @JsonIgnore
    var children: MutableList<StationData> = mutableListOf()

    @JsonIgnore
    var visited = false

    constructor(stationCode: String, stationName: String) : this() {
        this.stationCode = stationCode
        this.stationName = stationName
    }

    fun getLine(): String {
        return stationCode.substring(0, 2)
    }

    fun getStationNumber(): Int {
        return Integer.parseInt(stationCode.substring(2))
    }

    fun getUnvisitedChildren(): List<StationData>{
        return children.filter { !it.visited }
    }

    override fun toString(): String {
        return "{ stationCode=$stationCode, stationName=$stationName, visited=$visited, children=[" +
                children.joinToString(",") { it.stationCode } + "]},\n"
    }
}
