package com.dorokhov.ydtimerview.core

interface ITimerView {
    /** Sets the time in milliseconds
     * @param ms - milliseconds
     */
    fun setTime(ms: Long)
    fun startTimer()
    fun stopTimer()
    fun resetTimer()
}