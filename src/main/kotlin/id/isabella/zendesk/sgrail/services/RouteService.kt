package id.isabella.zendesk.sgrail.services

import id.isabella.zendesk.sgrail.builder.RouteStepsDataBuilder
import id.isabella.zendesk.sgrail.builder.StationDataMapBuilder
import id.isabella.zendesk.sgrail.builder.StationDateTimeBuilder
import id.isabella.zendesk.sgrail.exceptions.BadInputException
import id.isabella.zendesk.sgrail.exceptions.NotFoundException
import id.isabella.zendesk.sgrail.model.RouteStepsStationAmountData
import id.isabella.zendesk.sgrail.model.RouteStepsTimeDurationData
import id.isabella.zendesk.sgrail.model.StationData
import id.isabella.zendesk.sgrail.model.StationDateTime
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*


@Service
class RouteService {
    final val stationDatas: List<StationData> = StationDataMapBuilder.build()

    fun getRouteSteps(
        startStationName: String,
        endStationName: String,
        dateTime: LocalDateTime
    ): RouteStepsTimeDurationData {
        val stationDateTime = StationDateTimeBuilder.build(dateTime)
        var bestRoute: List<StationData> = getBestRoute(startStationName, endStationName, stationDateTime)
        return RouteStepsDataBuilder.buildRouteStepsTimeDurationData(bestRoute, stationDateTime)
    }

    fun getRouteSteps(startStationName: String, endStationName: String): RouteStepsStationAmountData {
        var bestRoute: List<StationData> = getBestRoute(startStationName, endStationName)
        return RouteStepsDataBuilder.buildRouteStepsStationAmountData(bestRoute)
    }

    private fun getBestRoute(
        startStationName: String,
        endStationName: String,
        stationDateTime: StationDateTime? = null
    ): List<StationData> {
        val (startStationData, endStationData) = getStartAndEndStation(startStationName, endStationName, stationDateTime)

        var bestRoute: List<StationData> = listOf()

        for (startStation in startStationData) {
            for (endStation in endStationData) {
                val record = getBestRouteFrom1StationTo1Station(startStation, endStation, stationDateTime)
                if (isBestRoute(record, bestRoute, stationDateTime)) {
                    bestRoute = record
                }
            }
        }
        return bestRoute
    }

    private fun isBestRoute(
        record: List<StationData>,
        bestRoute: List<StationData>,
        stationDateTime: StationDateTime?
    ) = !record.isNullOrEmpty() && (bestRoute.isEmpty() ||
            (stationDateTime != null &&
                    StationDataService.calculateDuration(record, stationDateTime) <
                    StationDataService.calculateDuration(bestRoute, stationDateTime)
                    ) ||
            (stationDateTime == null && record.size < bestRoute.size))

    private fun getStartAndEndStation(
        startStationName: String,
        endStationName: String,
        stationDateTime: StationDateTime?
    ): Pair<List<StationData>, List<StationData>> {
        val startStationData = stationDatas.filter { it.stationName == startStationName }
        val endStationData = stationDatas.filter { it.stationName == endStationName }
        validateStartAndEndStation(startStationData, startStationName, stationDateTime, endStationData, endStationName)
        return Pair(startStationData, endStationData)
    }

    private fun validateStartAndEndStation(
        startStationData: List<StationData>,
        startStationName: String,
        stationDateTime: StationDateTime?,
        endStationData: List<StationData>,
        endStationName: String
    ) {
        if (startStationData.isEmpty())
            throw NotFoundException("`$startStationName` start station is not found")
        if (endStationData.isEmpty())
            throw NotFoundException("`$endStationName` end station is not found")

        if ((stationDateTime == null && startStationData.none { !StationDataService.isStationClose(it) }) ||
            (stationDateTime != null && startStationData.none { !StationDataService.isStationClose(it, stationDateTime) }))
            throw BadInputException("`$startStationName` is close")
        if ((stationDateTime == null && endStationData.none { !StationDataService.isStationClose(it) }) ||
            (stationDateTime != null && endStationData.none { !StationDataService.isStationClose(it, stationDateTime) }))
            throw BadInputException("`$endStationName` is close")
    }

    // Getting best route by BFS algorithm
    private fun getBestRouteFrom1StationTo1Station(
        startStation: StationData,
        endStation: StationData,
        stationDateTime: StationDateTime?
    ): List<StationData> {
        val recordedFromStartRoutes = mutableListOf(listOf(startStation))
        var startQueue: PriorityQueue<Pair<Int, StationData>> = PriorityQueue(compareBy { it.first })
        if (stationDateTime != null) {
            startQueue = PriorityQueue(compareBy { it.first })
        }

        startQueue.add(Pair(0, startStation))

        while (!startQueue.isEmpty()) {
            val (priority, startNode) = startQueue.remove()
            startNode.visited = true

            if (endStation == startNode) {
                val record = recordedFromStartRoutes.find { it.last().stationCode == endStation.stationCode }
                refreshStationDatas()
                if (record.isNullOrEmpty()) return emptyList()
                return record
            }

            var unvisitedChildren = startNode.getUnvisitedChildren()
            if (stationDateTime != null) {
                unvisitedChildren = unvisitedChildren.filter { !StationDataService.isStationClose(it, stationDateTime) }
            } else {
                unvisitedChildren = unvisitedChildren.filter { !StationDataService.isStationClose(it) }
            }

            for (unvisited in unvisitedChildren) {
                addUnvisitedChildrenToQueue(
                    recordedFromStartRoutes,
                    startNode,
                    priority,
                    stationDateTime,
                    unvisited,
                    startQueue
                )
            }
        }
        refreshStationDatas()
        return emptyList()
    }

    private fun addUnvisitedChildrenToQueue(
        recordedFromStartRoutes: MutableList<List<StationData>>,
        startNode: StationData,
        priority: Int,
        stationDateTime: StationDateTime?,
        unvisited: StationData,
        startQueue: PriorityQueue<Pair<Int, StationData>>
    ) {
        val route = recordedFromStartRoutes.find { it.last().stationCode == startNode.stationCode }

        if (!route.isNullOrEmpty()) {
            var childrenPriority = priority + 1
            var deletedSizeRecorded = route.size
            if (stationDateTime != null) {
                childrenPriority += StationDataService.getDuration(startNode, unvisited, stationDateTime)
                deletedSizeRecorded -= 1
            }
            recordedFromStartRoutes.add(route.plus(unvisited))
            recordedFromStartRoutes.removeIf { it.size < deletedSizeRecorded }
            startQueue.add(Pair(childrenPriority, unvisited))
        }
    }


    fun refreshStationDatas() {
        stationDatas.filter { it.visited }.forEach { it.visited = false }
    }
}


