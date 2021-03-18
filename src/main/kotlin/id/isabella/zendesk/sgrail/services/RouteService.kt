package id.isabella.zendesk.sgrail.services

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import id.isabella.zendesk.sgrail.exceptions.NotFoundException
import id.isabella.zendesk.sgrail.model.RouteStepsData
import id.isabella.zendesk.sgrail.model.RouteStepsDataBuilder
import id.isabella.zendesk.sgrail.model.StationData
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import java.util.*


@Service
class RouteService {
    final val stationDatas: List<StationData>

    companion object {
        private const val STATION_MAP_FILE = "station_map.csv"
    }

    constructor() {
        val csvMapper = CsvMapper().apply {
            registerModule(KotlinModule())
        }
        val resource: Resource = ClassPathResource(STATION_MAP_FILE)

        stationDatas = csvMapper
            .readerFor(StationData::class.java)
            .with(CsvSchema.emptySchema().withHeader())
            .readValues<StationData>(resource.inputStream)
            .readAll()
        linkStationsWithNodeConcept()
    }

    private fun linkStationsWithNodeConcept() {
        for (i in stationDatas.indices) {
            linkSameLineNeighbourStation(i)
            linkTransitStation(i)
        }
    }

    private fun linkTransitStation(i: Int) {
        val linkedTransit =
            stationDatas.filter { it.stationName == stationDatas[i].stationName && it != stationDatas[i] }
        linkedTransit.forEach { stationDatas[i].children.add(it) }
    }

    private fun linkSameLineNeighbourStation(i: Int) {
        if ((i < stationDatas.size - 1) && stationDatas[i].getLine() == stationDatas[i + 1].getLine()) {
            stationDatas[i].children.add(stationDatas[i + 1])
            stationDatas[i + 1].children.add(stationDatas[i])
        }
    }

    fun getRouteSteps(startStationName: String, endStationName: String): RouteStepsData {
        val startStationData = stationDatas.filter { it.stationName == startStationName }
        val endStationData = stationDatas.filter { it.stationName == endStationName }
        if (startStationData.isEmpty())
            throw NotFoundException("`$startStationName` start station is not found")

        if (endStationData.isEmpty())
            throw NotFoundException("`$endStationName` end station is not found")

        var bestRoute: List<StationData> = listOf()

        for (startStation in startStationData) {
            for (endStation in endStationData) {
                val record = getBestRoute(startStation, endStation)
                if (!record.isNullOrEmpty() && (bestRoute.isEmpty() || record.size < bestRoute.size)) {
                    bestRoute = record
                }
            }
        }

        return RouteStepsDataBuilder.buildFromStationsData(bestRoute)
    }

    // Getting best route by BFS algorithm
    private fun getBestRoute(
        startStation: StationData,
        endStation: StationData
    ): List<StationData> {
        val startQueue: Queue<StationData> = LinkedList()
        startQueue.add(startStation)

        val recordedFromStartRoutes = mutableListOf(listOf(startStation))

        while (!startQueue.isEmpty()) {
            val startNode = startQueue.remove()
            startNode.visited = true

            if (endStation == startNode) {
                val record = recordedFromStartRoutes.find { it.last().stationCode == startNode.stationCode }
                refreshStationDatas()
                if (record.isNullOrEmpty()) return emptyList()
                return record
            }

            for (unvisited in startNode.getUnvisitedChildren()) {
                startQueue.add(unvisited)
                val route = recordedFromStartRoutes.find { it.last().stationCode == startNode.stationCode }
                if (!route.isNullOrEmpty()) {
                    recordedFromStartRoutes.add(route.plus(unvisited))
                    recordedFromStartRoutes.removeIf { it.size < route.size }
                }
            }
        }
        refreshStationDatas()
        return emptyList()
    }

    fun refreshStationDatas() {
        stationDatas.filter { it.visited }.forEach { it.visited = false }
    }
}