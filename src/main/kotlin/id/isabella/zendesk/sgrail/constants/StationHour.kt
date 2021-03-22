package id.isabella.zendesk.sgrail.constants

abstract class StationHour {
    abstract val TRANSIT_DURATION: Int
    abstract val EACH_SPECIAL_STATION_DURATION: Int
    abstract val DEFAULT_EACH_STATION_DURATION: Int
    abstract val SPECIAL_STATIONS: List<String>
}