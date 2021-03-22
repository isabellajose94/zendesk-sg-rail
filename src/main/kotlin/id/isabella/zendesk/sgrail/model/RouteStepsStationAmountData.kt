package id.isabella.zendesk.sgrail.model

class RouteStepsStationAmountData: RouteStepsData {
    val amountOfStations: Int
    constructor(amountOfStations: Int,  routes: List<String>, steps: List<String>) : super(routes, steps) {
        this.amountOfStations = amountOfStations
    }
}