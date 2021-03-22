package id.isabella.zendesk.sgrail.builder

import id.isabella.zendesk.sgrail.constants.NightHour
import id.isabella.zendesk.sgrail.constants.NormalHour
import id.isabella.zendesk.sgrail.constants.PeakHour
import id.isabella.zendesk.sgrail.constants.StationHourType
import id.isabella.zendesk.sgrail.exceptions.NotFoundException
import id.isabella.zendesk.sgrail.model.RouteStepsStationAmountData
import id.isabella.zendesk.sgrail.model.RouteStepsTimeDurationData
import id.isabella.zendesk.sgrail.model.StationData
import id.isabella.zendesk.sgrail.model.StationDateTime
import id.isabella.zendesk.sgrail.services.RouteService
import id.isabella.zendesk.sgrail.services.StationDataService

class RouteStepsDataBuilder {
    companion object {
        fun buildRouteStepsStationAmountData(stations: List<StationData>): RouteStepsStationAmountData {
            val amountOfStation = stations.size - 1
            val routes = mapRoutes(stations)
            val steps = mapSteps(stations)
            return RouteStepsStationAmountData(
                amountOfStation,
                routes,
                steps
            )
        }

        fun buildRouteStepsTimeDurationData(
            stations: List<StationData>,
            stationDateTime: StationDateTime
        ): RouteStepsTimeDurationData {
            val timeDuration = StationDataService.calculateDuration(stations, stationDateTime)
            val routes = mapRoutes(stations)
            val steps = mapSteps(stations)
            return RouteStepsTimeDurationData(
                timeDuration,
                routes,
                steps
            )
        }


        private fun mapSteps(stations: List<StationData>): List<String> {
            return stations.mapIndexedNotNull { index, stationData ->
                if (index < stations.size - 1)
                    if (StationDataService.isTransit(stationData, stations[index + 1]))
                        "Change from ${stationData.getLine()} line to ${stations[index + 1].getLine()} line"
                    else
                        "Take ${stationData.getLine()} line from ${stationData.stationName} to ${stations[index + 1].stationName}"
                else
                    null
            }
        }

        private fun mapRoutes(stations: List<StationData>): List<String> {
            return stations.map { it.stationCode }
        }
    }
}