package id.isabella.zendesk.sgrail.constants

import java.time.LocalTime

object PeakHour: StationHour() {
    override val TRANSIT_DURATION = 15
    override val DEFAULT_EACH_STATION_DURATION = 10
    override val EACH_SPECIAL_STATION_DURATION = 12
    override val SPECIAL_STATIONS = listOf("NS", "NE")

    val BEGIN_AM: LocalTime = LocalTime.of(6, 0)
    val FINISH_AM: LocalTime = LocalTime.of(9, 0)

    val BEGIN_PM: LocalTime = LocalTime.of(18, 0)
    val FINISH_PM: LocalTime = LocalTime.of(21, 0)
}