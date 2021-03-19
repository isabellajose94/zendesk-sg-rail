package id.isabella.zendesk.sgrail.model

class RouteStepsDataBuilder {
    companion object {
        fun buildFromStationsData(stations: List<StationData>): RouteStepsData {
            val amountOfStation = stations.size - 1
            val routes = stations.map { it -> it.stationCode }
            val steps = stations.mapIndexedNotNull { index, stationData ->
                if (index < stations.size - 1)
                    if (stationData.stationName == stations[index + 1].stationName)
                        "Change from ${stationData.getLine()} line to ${stations[index + 1].getLine()} line"
                    else
                        "Take " + stationData.getLine() + " line from ${stationData.stationName} to ${stations[index + 1].stationName}"
                else
                    null
            }
            return RouteStepsData(
                amountOfStation,
                routes,
                steps
            )
        }
    }
}