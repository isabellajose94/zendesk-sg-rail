package id.isabella.zendesk.sgrail.model

class RouteStepsTimeDurationData: RouteStepsData {
    val durationTime: Int
    constructor(durationTime: Int, routes: List<String>, steps: List<String>) : super(routes, steps) {
        this.durationTime = durationTime
    }
}