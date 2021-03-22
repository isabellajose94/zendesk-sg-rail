package id.isabella.zendesk.sgrail.builder

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import id.isabella.zendesk.sgrail.model.StationData
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import com.fasterxml.jackson.databind.SerializationFeature

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class StationDataMapBuilder {
    companion object {
        private const val STATION_MAP_FILE = "station_map.csv"

        fun build(): List<StationData> {
            val csvMapper = CsvMapper().apply {
                registerModules(KotlinModule(), JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            }
            val resource: Resource = ClassPathResource(STATION_MAP_FILE)

            val stationDatas = csvMapper
                .readerFor(StationData::class.java)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues<StationData>(resource.inputStream)
                .readAll()
            linkStationsWithNodeConcept(stationDatas)
            return stationDatas
        }

        private fun linkStationsWithNodeConcept(stationDatas: List<StationData>) {
            for (i in stationDatas.indices) {
                linkSameLineNeighbourStation(stationDatas, i)
                linkTransitStation(stationDatas, i)
            }
        }

        private fun linkTransitStation(stationDatas: List<StationData>, i: Int) {
            val linkedTransit =
                stationDatas.filter { it.stationName == stationDatas[i].stationName && it != stationDatas[i] }
                linkedTransit.forEach { stationDatas[i].children.add(it) }
        }


        private fun linkSameLineNeighbourStation(stationDatas: List<StationData>, i: Int) {
            if ((i < stationDatas.size - 1) &&
                stationDatas[i].getLine() == stationDatas[i + 1].getLine()
            ) {
                stationDatas[i].children.add(stationDatas[i + 1])
                stationDatas[i + 1].children.add(stationDatas[i])
            }
        }
    }

}