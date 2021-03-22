package id.isabella.zendesk.sgrail.constants

import java.time.LocalTime

object NightHour: StationHour() {
    val BEGIN: LocalTime = LocalTime.of(22, 0)
    val FINISH: LocalTime = LocalTime.of(6, 0)
    val CLOSED_LINES = listOf("CG", "DT", "CE")
    override val TRANSIT_DURATION = 10
    override val EACH_SPECIAL_STATION_DURATION = 8
    override val DEFAULT_EACH_STATION_DURATION = 10
    override val SPECIAL_STATIONS = listOf("TE")
}