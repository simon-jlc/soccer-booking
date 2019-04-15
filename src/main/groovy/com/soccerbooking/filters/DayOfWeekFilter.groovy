package com.soccerbooking.filters

import com.soccerbooking.CenterFilter
import com.soccerbooking.urban.Center

import java.time.DayOfWeek
import java.time.ZoneId

class DayOfWeekFilter implements CenterFilter {

    Set<DayOfWeek> keptDays

    DayOfWeekFilter(final OptionAccessor options) {
        keptDays = configure(options)
    }

    @Override
    boolean apply(final Center center) {
        def time = center.start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        return keptDays.contains(time.dayOfWeek)
    }

    private Set<DayOfWeek> configure(final OptionAccessor options) {
        def daysOfWeekFilter = options.'filterDayOfWeek' as String
        if (!daysOfWeekFilter) {
            return []
        }

        return daysOfWeekFilter
                .split(",")
                .collect { DayOfWeek.valueOf(it) } as Set
    }
}
