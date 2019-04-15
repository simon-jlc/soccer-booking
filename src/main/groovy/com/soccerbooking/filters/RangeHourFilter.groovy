package com.soccerbooking.filters

import com.soccerbooking.CenterFilter
import com.soccerbooking.urban.Center

import java.time.LocalTime
import java.time.ZoneId

/**
 * Applying center filter according --filterRangeHour option.
 */
class RangeHourFilter implements CenterFilter {

    LocalTime minTime
    LocalTime maxTime

    RangeHourFilter(final OptionAccessor options) {
        def filterRangeHour = options.'filterRangeHour' as String
        String[] range = filterRangeHour.split('-')
        minTime = LocalTime.parse(range[0])
        maxTime = LocalTime.parse(range[1])
    }

    @Override
    boolean apply(final Center center) {
        def startDateTime = center.start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        def startTime = LocalTime.of(startDateTime.hour, startDateTime.minute)
        return startTime.isAfter(minTime) && startTime.isBefore(maxTime)
    }
}
