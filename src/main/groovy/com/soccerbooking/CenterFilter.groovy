package com.soccerbooking

import com.soccerbooking.urban.Center

interface CenterFilter {

    /**
     * Filter Center result according criteria (date, center, range hour)
     *
     * @param center
     * @return
     */
    boolean apply(final Center center)

}