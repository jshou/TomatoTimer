package com.joshuahou.tomatotimer.tomatotimer

import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val WORK_AMOUNT = (25 * 60 * 1000).toLong()
        const val REST_AMOUNT = (5 * 60 * 1000).toLong()
    }

    private lateinit var timer: CountDownTimer
    private lateinit var audioPlayer: MediaPlayer
    private lateinit var beepFile: AssetFileDescriptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        volumeControlStream = AudioManager.STREAM_ALARM
        beepFile = assets.openFd("beep.mp3")

        audioPlayer = MediaPlayer()
        val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        audioPlayer.setAudioAttributes(attributes)

        setupAudio()
        setupTimer(0)
    }

    fun cancel(view: View) {
        cancelButton.isEnabled = false
        resetWorkButton.isEnabled = true
        resetRestButton.isEnabled = true
        mode.text = getString(R.string.smile)
        timerDisplay.text = getString(R.string.zero)
        timer.cancel()
    }

    fun work(view: View) {
        reset(view)
        setupTimer(WORK_AMOUNT)
        mode.text = getString(R.string.work_mode)
        timer.start()
    }

    fun rest(view: View) {
        reset(view)
        setupTimer(REST_AMOUNT)
        mode.text = getString(R.string.rest_mode)
        timer.start()
    }

    private fun reset(view: View) {
        cancelButton.isEnabled = true
        resetWorkButton.isEnabled = false
        resetRestButton.isEnabled = false
        timer.cancel()
        resetAudio()
    }


    private fun setupTimer(amount: Long) {
        timer = object: CountDownTimer(amount, 500) {
            override fun onFinish() {
                onTimerFinish()
            }

            override fun onTick(millisUntilFinished: Long) {
                timerDisplay.text = formatTime(millisUntilFinished)
            }
        }
        timerDisplay.text = formatTime(amount)
    }

    fun onTimerFinish() {
        Toast.makeText(this, getString(R.string.finished_timer_message), Toast.LENGTH_SHORT).show()
        audioPlayer.start()
    }

    private fun setupAudio() {
        audioPlayer.setDataSource(beepFile.fileDescriptor, beepFile.startOffset, beepFile.length)
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
