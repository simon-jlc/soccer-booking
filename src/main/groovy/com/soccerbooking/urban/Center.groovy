package com.soccerbooking.urban

class Center {
    String section
    String centerName
    String durationDisplay
    int price
    String resourceTypeDisplay
    Date start

    @Override
    String toString() {
        "[$section] $start - $resourceTypeDisplay / $durationDisplay - $centerName ($price EUR)"
    }

    static Center of(final String section, final Map centerAttr) {
        new Center(
                section: section,
                centerName: centerAttr.centerName,
                durationDisplay: centerAttr.durationDisplay,
                resourceTypeDisplay: centerAttr.resourceTypeDisplay,
                start: Date.parse("yyyy-MM-dd'T'HH:mm:ss", centerAttr.start),
                price: centerAttr.price
        )
    }
}
