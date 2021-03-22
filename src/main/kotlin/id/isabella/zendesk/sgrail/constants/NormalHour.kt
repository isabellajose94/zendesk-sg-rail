package id.isabella.zendesk.sgrail.constants

object NormalHour: StationHour() {
    override val TRANSIT_DURATION = 10
    override val DEFAULT_EACH_STATION_DURATION = 10
    override val EACH_SPECIAL_STATION_DURATION = 8
    override val SPECIAL_STATIONS = listOf("DT", "TE")
}