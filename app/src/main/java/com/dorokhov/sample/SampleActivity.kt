package com.dorokhov.sample

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.dorokhov.timerview.R
import com.dorokhov.ydtimerview.core.TimeUnitTimer
import com.dorokhov.ydtimerview.core.TimerView
import com.dorokhov.ydtimerview.listeners.TimerListener

class SampleActivity : AppCompatActivity(), TimerListener {

    lateinit var timerView: TimerView
    lateinit var timeEt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_sample)

        timerView = findViewById(R.id.timer)
        timeEt = findViewById(R.id.timeEditText)

        timerView.setTimerListener(this)

        findViewById<Button>(R.id.startbtn).setOnClickListener {
            timerView.startTimer(false)
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            timerView.stopTimer(false)
        }

        findViewById<Button>(R.id.resetButton).setOnClickListener {
            timerView.resetTimer()
        }

        findViewById<Button>(R.id.setFullTimeForTimer).setOnClickListener {
            if (timeEt.text.toString().isNotEmpty() && timeEt.text.toString().isDigitsOnly()) {
                timerView.setTime(timeEt.text.toString().toLong(), TimeUnitTimer.SECONDS)
            }
        }

        findViewById<Button>(R.id.setCurrentTimeForTimer).setOnClickListener {
            if (timeEt.text.toString().isNotEmpty() && timeEt.text.toString().isDigitsOnly()) {
                timerView.setCurrentTime(timeEt.text.toString().toLong(), TimeUnitTimer.SECONDS)
            }
        }

    }

    override fun onTimerComplete() {
        Toast.makeText(this, "Таймер завершил свою работу", Toast.LENGTH_SHORT).show()
    }
}