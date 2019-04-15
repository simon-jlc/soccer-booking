package com.soccerbooking.filters

import com.soccerbooking.CenterFilter
import com.soccerbooking.urban.Center
import com.soccerbooking.urban.Centers

class OnlyAskedCenterFilter implements CenterFilter {

    Set<Centers> keptCenters

    OnlyAskedCenterFilter(final OptionAccessor options) {
        keptCenters = []
        keptCenters.addAll(Centers.parseOptions(options))
    }

    @Override
    boolean apply(Center center) {
        return keptCenters.contains()
    }
}
