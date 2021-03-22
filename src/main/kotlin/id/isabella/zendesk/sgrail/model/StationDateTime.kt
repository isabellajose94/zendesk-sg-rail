package id.isabella.zendesk.sgrail.model

import id.isabella.zendesk.sgrail.constants.StationHourType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class StationDateTime {
    val dateTime: LocalDateTime
    val stationHourType: StationHourType
    val date: LocalDate
    val time: LocalTime
    constructor(dateTime: LocalDateTime, stationHourType: StationHourType) {
        this.dateTime = dateTime
        this.stationHourType = stationHourType
        this.date = dateTime.toLocalDate()
        this.time = dateTime.toLocalTime()
    }
}