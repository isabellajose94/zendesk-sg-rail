package id.isabella.zendesk.sgrail.services

import id.isabella.zendesk.sgrail.constants.NightHour
import id.isabella.zendesk.sgrail.constants.NormalHour
import id.isabella.zendesk.sgrail.constants.PeakHour
import id.isabella.zendesk.sgrail.constants.StationHourType
import id.isabella.zendesk.sgrail.exceptions.NotFoundException
import id.isabella.zendesk.sgrail.model.StationData
import id.isabella.zendesk.sgrail.model.StationDateTime
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

class StationDataService {
    companion object {
        fun isNightHour(time: LocalTime): Boolean {
            return (time.isAfter(NightHour.BEGIN) || time == NightHour.BEGIN) ||
                    (time.isBefore(NightHour.FINISH) || time == NightHour.FINISH)
        }

        fun isPeakHour(dateTime: LocalDateTime): Boolean {
            val time = dateTime.toLocalTime()
            return dateTime.dayOfWeek >= DayOfWeek.MONDAY && dateTime.dayOfWeek <= DayOfWeek.FRIDAY && (
                    (
                            (time.isAfter(PeakHour.BEGIN_AM) || time.equals(PeakHour.BEGIN_AM)) &&
                                    (time.isBefore(PeakHour.FINISH_AM) || time.equals(PeakHour.FINISH_AM))
                            ) || (
                            (time.isAfter(PeakHour.BEGIN_PM) || time.equals(PeakHour.BEGIN_PM)) &&
                                    (time.isBefore(PeakHour.FINISH_PM) || time.equals(PeakHour.FINISH_PM))
                            )
                    )

        }
        fun isTransit(
            fromStation: StationData,
            toStation: StationData
        ) = fromStation.stationName == toStation.stationName

        fun isStationClose(station: StationData, stationDateTime: StationDateTime): Boolean {
            return (stationDateTime.date.isBefore(station.openingDate)) ||
                    (stationDateTime.stationHourType == StationHourType.NIGHT_HOUR &&
                            NightHour.CLOSED_LINES.indexOf(station.getLine()) >= 0)
        }

        fun calculateDuration(stations: List<StationData>, stationDateTime: StationDateTime): Int {
            return stations.mapIndexedNotNull { index, stationData ->
                if (index < stations.size - 1)
                    getDuration(stationData, stations[index + 1], stationDateTime)
                else
                    null
            }.sum()
        }

        fun getDuration(fromStation: StationData, toStation: StationData, stationDateTime: StationDateTime): Int {
            when (stationDateTime.stationHourType) {
                StationHourType.NIGHT_HOUR -> {
                    if (isTransit(fromStation, toStation))
                        return NightHour.TRANSIT_DURATION
                    if (NightHour.SPECIAL_STATIONS.contains(toStation.getLine()))
                        return NightHour.EACH_SPECIAL_STATION_DURATION
                    return NightHour.DEFAULT_EACH_STATION_DURATION
                }
                StationHourType.PEAK_HOUR -> {
                    if (isTransit(fromStation, toStation))
                        return PeakHour.TRANSIT_DURATION
                    if (PeakHour.SPECIAL_STATIONS.contains(toStation.getLine()))
                        return PeakHour.EACH_SPECIAL_STATION_DURATION
                    return PeakHour.DEFAULT_EACH_STATION_DURATION
                }
                StationHourType.NORMAL_HOUR -> {
                    if (isTransit(fromStation, toStation))
                        return NormalHour.TRANSIT_DURATION
                    if (NormalHour.SPECIAL_STATIONS.contains(toStation.getLine()))
                        return NormalHour.EACH_SPECIAL_STATION_DURATION
                    return NormalHour.DEFAULT_EACH_STATION_DURATION
                }
            }
            throw NotFoundException("Type of hour can't found - ${stationDateTime.stationHourType}")
        }
    }


}