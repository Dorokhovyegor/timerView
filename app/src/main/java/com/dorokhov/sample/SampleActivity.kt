package com.dorokhov.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dorokhov.timerview.R
import com.dorokhov.ydtimerview.core.TimerView

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_sample)
        findViewById<TimerView>(R.id.timer).startTimer()
    }
}