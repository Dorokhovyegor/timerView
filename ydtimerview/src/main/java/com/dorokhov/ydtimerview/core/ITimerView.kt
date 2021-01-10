package com.dorokhov.ydtimerview.core

interface ITimerView {
    /** Sets the time in milliseconds
     * @param time: time
     * @param unit: timeUnit: ms, seconds, minutes, hours
     * ms by default
     */
    fun setTime(time: Long, unit: TimeUnitTimer)
    fun setTime(time: Long)
    /**
     * @param withReset if true, the current time sets to 0, before the starting
     * */
    fun startTimer(withReset: Boolean)
    /**
     * @param withReset if true, the current time sets to 0, after the stoping
     * */
    fun stopTimer(withReset: Boolean)
    /**
     * set time to 0
     * */
    fun resetTimer()
    /**
     * sets current time to timer
     * */
    fun setCurrentTime(time: Long, unit: TimeUnitTimer)
}