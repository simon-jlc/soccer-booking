package com.soccerbooking.filters

import com.soccerbooking.CenterFilter
import com.soccerbooking.urban.Center
import com.soccerbooking.urban.CenterDesc

class OnlyAskedCenterFilter implements CenterFilter {

    Set<CenterDesc> keptCenters

    OnlyAskedCenterFilter(final OptionAccessor options) {
        keptCenters = []
        keptCenters.addAll(CenterDesc.parseOptions(options))
    }

    @Override
    boolean apply(Center center) {
        return keptCenters.contains(CenterDesc.findById(center.centerId))
    }
}
