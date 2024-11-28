package com.bekircaglar.bluchat.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.os.Vibrator
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException

class AudioRecorderManager(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null

    fun startRecording(): String {
        val audioFile = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "audio_record.3gp")
        audioFilePath = audioFile.absolutePath

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFilePath)
            try {
                prepare()
                start()
            } catch (e: IOException) {
                audioFilePath = null
            }
        }

        return audioFilePath ?: ""
    }

    fun stopRecording(): String? {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            return audioFilePath
        } catch (e: RuntimeException) {
            return null
        }
    }

    fun playAudio(filePath: String, onCompletion: (() -> Unit)? = null) {
        try {
            MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
                setOnCompletionListener {
                    it.release()
                    onCompletion?.invoke()
                }
            }
        } catch (e: Exception) {

        }
    }
}

class PermissionManager(private val context: Context) {
    fun hasAudioRecordPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }
}

object VibrationUtils {
    fun vibrate(context: Context, duration: Long = 50) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.vibrate(duration)
    }

    fun vibratePattern(context: Context, pattern: LongArray, repeat: Int = -1) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.vibrate(pattern, repeat)
    }
}