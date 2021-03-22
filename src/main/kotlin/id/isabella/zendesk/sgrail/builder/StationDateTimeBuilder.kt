package id.isabella.zendesk.sgrail.builder

import id.isabella.zendesk.sgrail.constants.StationHourType
import id.isabella.zendesk.sgrail.model.StationDateTime
import id.isabella.zendesk.sgrail.services.StationDataService
import java.time.LocalDateTime

class StationDateTimeBuilder {
    companion object {
        fun build(dateTime: LocalDateTime): StationDateTime {
            var stationHourType = StationHourType.NORMAL_HOUR
            if (StationDataService.isNightHour(dateTime.toLocalTime()))
                stationHourType = StationHourType.NIGHT_HOUR
            else if (StationDataService.isPeakHour(dateTime))
                stationHourType = StationHourType.PEAK_HOUR
            return StationDateTime(dateTime, stationHourType)
        }
    }
}