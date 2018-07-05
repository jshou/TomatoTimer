package com.joshuahou.tomatotimer.tomatotimer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileDescriptor

class MainActivity : AppCompatActivity() {
    companion object {
        const val TOMATO_AMOUNT = (0.05 * 60 * 1000).toLong()
        const val REST_AMOUNT = 5 * 60 * 1000
    }

    private var timer: CountDownTimer = object: CountDownTimer(TOMATO_AMOUNT, 1000) {
        override fun onFinish() {
            onTimerFinish()
        }

        override fun onTick(millisUntilFinished: Long) {
            timerDisplay.text = formatTime(millisUntilFinished)
        }
    }

    private lateinit var audioPlayer: MediaPlayer
    private lateinit var beepFile: FileDescriptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        volumeControlStream = AudioManager.STREAM_ALARM
        beepFile = resources.openRawResourceFd(R.raw.beep).fileDescriptor

        audioPlayer = MediaPlayer()
        val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        audioPlayer.setAudioAttributes(attributes)
        setupAudio()
    }

    fun start(view: View) {
        timer.start()
        startButton.isEnabled = false
        resetButton.isEnabled = true
        Toast.makeText(this, getString(R.string.start_message), Toast.LENGTH_SHORT).show()
    }

    fun reset(view: View) {
        timer.cancel()
        startButton.isEnabled = true
        resetButton.isEnabled = false
        timerDisplay.text = formatTime(TOMATO_AMOUNT)
        resetAudio()
        Toast.makeText(this, getString(R.string.reset_message), Toast.LENGTH_SHORT).show()
    }

    fun onTimerFinish() {
        Toast.makeText(this, getString(R.string.finished_timer_message), Toast.LENGTH_SHORT).show()
        audioPlayer.start()
    }

    private fun setupAudio() {
        audioPlayer.setDataSource(beepFile)
        audioPlayer.prepare()
    }

    private fun resetAudio() {
        audioPlayer.reset()
        setupAudio()
    }

    private fun formatTime(millis: Long): String {
        val minutes = millis / 1000 / 60
        val seconds = (millis / 1000 % 60).toString().padStart(2, '0')

        return "$minutes:$seconds"
    }
}
