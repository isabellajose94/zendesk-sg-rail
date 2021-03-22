package id.isabella.zendesk.sgrail.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
class StationData() {
    @JsonAlias("Station Code")
    lateinit var stationCode: String

    @JsonAlias("Station Name")
    lateinit var stationName: String

    @JsonAlias("Opening Date")
    @JsonFormat(pattern = "d MMMM yyyy")
    lateinit var openingDate: LocalDate

    @JsonIgnore
    var children: MutableList<StationData> = mutableListOf()

    @JsonIgnore
    var visited = false

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
        return "{ stationCode=$stationCode, stationName=$stationName, openingDate=$openingDate, visited=$visited, children=[" +
                children.joinToString(",") { it.stationCode } + "]},\n"
    }
}
