package com.dorokhov.ydtimerview.core

sealed class TimeUnitTimer() {
    object MILLISECONDS: TimeUnitTimer()
    object SECONDS: TimeUnitTimer()
    object MINUTES: TimeUnitTimer()
    object HOURS: TimeUnitTimer()
}