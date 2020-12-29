package com.dorokhov.sample

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.dorokhov.timerview.R
import com.dorokhov.timerview.TimerView

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.act_sample)
        findViewById<TimerView>(R.id.timerView).apply {
            setTime(1200)
        }
    }
}