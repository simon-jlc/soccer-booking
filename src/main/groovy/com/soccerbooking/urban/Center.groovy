package com.soccerbooking.urban

class Center {
    String centerName
    String durationDisplay
    int price
    String resourceTypeDisplay
    String start

    @Override
    String toString() {
        "$start - $resourceTypeDisplay / $durationDisplay - $centerName ($price EUR)"
    }
}
